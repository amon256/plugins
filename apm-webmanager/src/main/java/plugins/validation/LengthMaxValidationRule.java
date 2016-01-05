/**
 * 
 */
package plugins.validation;

/**
 * @author fengmengyue
 *
 */
public class LengthMaxValidationRule implements ValidationRule {
	
	private int maxLength;
	
	public LengthMaxValidationRule(int maxLength){
		this.maxLength = maxLength;
	}

	@Override
	public String type() {
		return "lengmax";
	}

	@Override
	public boolean validate(Object bean, Object value) {
		if(value != null && (value instanceof String)){
			int len = value.toString().trim().length();
			return len <= maxLength;
		}
		return true;
	}

	@Override
	public String getErrorMessage() {
		return "最大长度"+maxLength;
	}

}
