package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Expression;

public abstract class AbstractIsEqualTo extends BinaryObjectResultingInBooleanQuestion {
	protected boolean isEqualTo( Object aValue, Object bValue ) {
		if( aValue != null ) {				
			if( aValue.equals( bValue ) ) {
                return true;
            }
			if( aValue instanceof Expression ) {
			    Expression aExpression = (Expression)aValue;
			    Object aValue2 = aExpression.getValue();
			    if( bValue instanceof Expression ) {
			        Expression bExpression = (Expression)bValue;
			        Object bValue2 = bExpression.getValue();
			        if( aExpression.equals( bExpression.getValue() ) ) {
			            return true;
			        }
					if( aValue2 != null ) {
					    if( aValue2.equals( bExpression ) ) {
					        return true;
					    }
						return aValue2.equals( bValue2 );
					}
					return bValue2==null;
			    }
				if( aValue2 != null ) {
				    return aValue2.equals( bValue );
				}
				return bValue == null;
			}
			if( bValue instanceof Expression ) {
			    Expression bExpression = (Expression)bValue;
			    Object bValue2 = bExpression.getValue();
			    if( aValue.equals( bExpression ) ) {
			        return true;
			    }
				return aValue.equals( bValue2 );
			}
			try {
				double a = Double.valueOf(aValue.toString());
				double b = Double.valueOf(bValue.toString());			
				if ( a == b ) return true;
				return false;
			} catch (Exception e) {
				return false;
			}
		}
		if( bValue instanceof Expression ) {
		    Expression bExpression = (Expression)bValue;
		    Object bValue2 = bExpression.getValue();
		    return bValue2 == null;
		}
		return bValue == null;
	}
}
