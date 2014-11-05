/**
 * HttpRequestWrap.java.java
 * @author FengMy
 * @since 2014年10月30日
 */
package plugin.http.server;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2014年10月30日
 */
public abstract class HttpRequestWrap implements HttpRequest {
	
	protected InputStream inputstream;//请求输入流
	protected HttpMethodEnum method;//请求方式
	protected String requestURI;//请求地址
	protected Map<String,String> heads = new HashMap<String, String>();//http头信息
	protected Map<String,String> parameters = new HashMap<String, String>();//参数信息
	protected String clientAddress;//客户地址
	public HttpRequestWrap(InputStream inputstream) throws IOException{
		if(inputstream == null){
			throw new RuntimeException("inputstream is required.");
		}
		this.inputstream = inputstream;
		readFromInputStream();
	}
	
	private void readFromInputStream() throws IOException{
		if(this.inputstream.markSupported()){
			this.inputstream.reset();
		}
		String readLine = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(inputstream));
		do{
			readLine = br.readLine();
		}while(readLine == null || readLine.length() == 0);
		//读取第一行，为方法头method url prototype
		readHttpMethodAndURI(readLine);
		while(true){
			readLine = br.readLine();
			if(readLine == null || readLine.length() == 0){
				break;
			}
			int splitIndex = readLine.indexOf(":");
			if(splitIndex <= 0){
				throw new RuntimeException("invalid http head: " + readLine);
			}
			this.heads.put(readLine.substring(0, splitIndex), readLine.substring(splitIndex + 1).trim());
		}
//		"Content-Length", "Content-Type"
		if(this.heads.containsKey("Content-Length")){
			int contentLength = Integer.parseInt(this.heads.get("Content-Length"));
			if(contentLength > 0){
				//TODO
				byte[] content = new byte[contentLength];
				int len = 0;
				int index = 0;
				byte[] buff = new byte[1024];
				while((len = inputstream.read(buff)) > 0){
					for(int i = 0; i < len; i++){
						index++;
						content[index] = buff[i];
					}
				}
				System.out.println(content.length);
			}
		}
	}
	
	/**
	 * 读取http method 和uri
	 * @param line
	 */
	private void readHttpMethodAndURI(String line){
		String[] headValues = line.split("\\s");
		if(headValues.length < 2){
			throw new RuntimeException("invalid http head." + line);
		}
		if(HttpMethodEnum.GET.name().equals(headValues[0])){
			this.method = HttpMethodEnum.GET;
		}else if(HttpMethodEnum.POST.name().equals(headValues[0])){
			this.method = HttpMethodEnum.POST;
		}else{
			throw new RuntimeException("unknow http method:" + headValues[0]);
		}
		
		this.requestURI = headValues[1];
	}
	
	

	@Override
	public HttpMethodEnum getMethod() {
		return this.method;
	}

	@Override
	public String getHead(String name) {
		return this.heads==null?null:this.heads.get(name);
	}

	@Override
	public Map<String, String> getHeads() {
		return this.heads==null?null:new HashMap<String, String>(this.heads);
	}

	@Override
	public String getParameter(String name) {
		return this.parameters==null?null:this.parameters.get(name);
	}

	@Override
	public Map<String, String> getParameters() {
		return this.parameters==null?null:new HashMap<String, String>(this.parameters);
	}

	@Override
	public InputStream getInputStream() {
		return this.inputstream;
	}

	@Override
	public String getClientAddress() {
		return clientAddress;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	@Override
	public String getRequestURI() {
		return this.requestURI;
	}
}
