/**
 * MemoryHttpHandler.java.java
 * @author FengMy
 * @since 2015年11月30日
 */
package plugins.monitortools.server.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import plugins.monitortools.server.ContentTypes;

import com.alibaba.fastjson.JSON;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年11月30日
 */
public class ResourceUsageHttpHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		Map<String,Number> result = new HashMap<String, Number>();
		long time = new Date().getTime();
		MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
		int mb = 1024*1024;
		result.put("time", time);
		result.put("heapUsage", (int) (memory.getHeapMemoryUsage().getUsed() / mb));
		result.put("nonHeapUsage", (int) (memory.getNonHeapMemoryUsage().getUsed() / mb));
		
		String osName = System.getProperty("os.name");
		double cpuRatio = 0;
        if (osName.toLowerCase().startsWith("windows")) {
            cpuRatio = this.getCpuRatioForWindows();
		} else {
			cpuRatio = this.getCpuRateForLinux();
        }
        
        result.put("cpuUsage", cpuRatio);
        String json = JSON.toJSONString(result);
		exchange.getResponseHeaders().add(ContentTypes.CONTENT_TYPE, "text/json;charset=UTF-8");
		exchange.sendResponseHeaders(200, json.getBytes("utf-8").length);
		OutputStream os = exchange.getResponseBody();
		os.write(json.getBytes("utf-8"));
		os.flush();
		os.close();
	}
	private double getCpuRateForLinux(){
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader brStat = null;
        StringTokenizer tokenStat = null;
        try{
            Process process = Runtime.getRuntime().exec("top -b -n 1");
            is = process.getInputStream();                    
            isr = new InputStreamReader(is);
            brStat = new BufferedReader(isr);
            
            brStat.readLine();
            brStat.readLine();
                
            tokenStat = new StringTokenizer(brStat.readLine());
            tokenStat.nextToken();
            tokenStat.nextToken();
            tokenStat.nextToken();
            tokenStat.nextToken();
            tokenStat.nextToken();
            tokenStat.nextToken();
            tokenStat.nextToken();
            String cpuUsage = tokenStat.nextToken();
                
            
            Float usage = new Float(cpuUsage.substring(0,cpuUsage.indexOf("%")));
            
            return (1-usage.floatValue()/100);
             
        } catch(IOException ioe){
            freeResource(is, isr, brStat);
            return 1;
        } finally{
            freeResource(is, isr, brStat);
        }
    }
	
	private static void freeResource(InputStream is, InputStreamReader isr, BufferedReader br){
        try{
            if(is!=null)
                is.close();
            if(isr!=null)
                isr.close();
            if(br!=null)
                br.close();
        }catch(IOException ioe){
        	throw new RuntimeException(ioe);
        }
    }
	 /** 
     * 获得CPU使用率.
     * @return 返回cpu使用率
     */
    private double getCpuRatioForWindows() {
        try {
            String procCmd = System.getenv("windir")
                    + "\\system32\\wbem\\wmic.exe process get Caption,CommandLine,"
                    + "KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount";
            // 取进程信息
            long[] c0 = readCpu(Runtime.getRuntime().exec(procCmd));
            Thread.sleep(30);
            long[] c1 = readCpu(Runtime.getRuntime().exec(procCmd));
            if (c0 != null && c1 != null) {
                long idletime = c1[0] - c0[0];
                long busytime = c1[1] - c0[1];
                return Double.valueOf(100 * (busytime) / (busytime + idletime))
                        .doubleValue();
            } else {
                return 0.0;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0.0;
        }
    }
    /**    
 * 读取CPU信息.
     * @param proc
     */
    private long[] readCpu(final Process proc) {
        long[] retn = new long[2];
        try {
            proc.getOutputStream().close();
            InputStreamReader ir = new InputStreamReader(proc.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line = input.readLine();
            if (line == null || line.length() < 10) {
                return null;
            }
            int capidx = line.indexOf("Caption");
            int cmdidx = line.indexOf("CommandLine");
            int rocidx = line.indexOf("ReadOperationCount");
            int umtidx = line.indexOf("UserModeTime");
            int kmtidx = line.indexOf("KernelModeTime");
            int wocidx = line.indexOf("WriteOperationCount");
            long idletime = 0;
            long kneltime = 0;
            long usertime = 0;
            while ((line = input.readLine()) != null) {
                if (line.length() < wocidx) {
                    continue;
                }
                // 字段出现顺序：Caption,CommandLine,KernelModeTime,ReadOperationCount,
                // ThreadCount,UserModeTime,WriteOperation
                String caption = Bytes.substring(line, capidx, cmdidx - 1)
                        .trim();
                String cmd = Bytes.substring(line, cmdidx, kmtidx - 1).trim();
                if (cmd.indexOf("wmic.exe") >= 0) {
                    continue;
                }
                // log.info("line="+line);
                if (caption.equals("System Idle Process")
                        || caption.equals("System")) {
                    idletime += Long.valueOf(
                            Bytes.substring(line, kmtidx, rocidx - 1).trim())
                            .longValue();
                    idletime += Long.valueOf(
                            Bytes.substring(line, umtidx, wocidx - 1).trim())
                            .longValue();
                    continue;
                }
                kneltime += Long.valueOf(
                        Bytes.substring(line, kmtidx, rocidx - 1).trim())
                        .longValue();
                usertime += Long.valueOf(
                        Bytes.substring(line, umtidx, wocidx - 1).trim())
                        .longValue();
            }
            retn[0] = idletime;
            retn[1] = kneltime + usertime;
            return retn;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                proc.getInputStream().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

class Bytes {
    public static String substring(String src, int start_idx, int end_idx){
        byte[] b = src.getBytes();
        String tgt = "";
        for(int i=start_idx; i<=end_idx; i++){
            tgt +=(char)b[i];
        }
        return tgt;
    }
}
