/**
 * 
 */
package plugins.datasource.readwrite;

import java.lang.reflect.Method;
import java.util.LinkedList;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Aspect
@Component
public class ReadWriteProcessor implements Ordered{
	
	private static final ThreadLocal<LinkedList<Boolean>> localThread = new ThreadLocal<LinkedList<Boolean>>();

	@Around(value="execution(public * com.monte.mybatis.rwsplit.service.*.*(*))")
	public Object readWriteProcess(ProceedingJoinPoint point) throws Throwable{
		LinkedList<Boolean> readWriteStack = localThread.get();
		if(readWriteStack == null){
			readWriteStack = new LinkedList<Boolean>();
		}
		boolean readOnly = !readWriteStack.contains(false) && isReadOnly(point);
		readWriteStack.push(readOnly);
		localThread.set(readWriteStack);
		if(readOnly){
			ReadWriteHolder.setReadOnly();//设置只读
		}
		Object result = point.proceed();
		readWriteStack = localThread.get();
		readWriteStack.pop();
		if(readWriteStack.isEmpty()){
			ReadWriteHolder.reset();//重置线程变量
		}
		return result;
	}
	
	private boolean isReadOnly(ProceedingJoinPoint point){
		MethodSignature signature = (MethodSignature) point.getSignature();
		Method method = signature.getMethod();
		Transactional tran = method.getAnnotation(Transactional.class);
		if(tran != null && tran.readOnly()){
			return true;
		}
		Class<?> clazz = point.getTarget().getClass();
		clazz.getAnnotation(Transactional.class);
		return false;
	}

	@Override
	public int getOrder() {
		return Integer.MIN_VALUE;
	}
}
