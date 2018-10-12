/*
 * Copyright (c) 1999-2003, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice",
 *    nor may "Alice" appear in their name, without prior written
 *    permission of Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    "This product includes software developed by Carnegie Mellon University"
 */

package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.property.ClassProperty;
import edu.cmu.cs.stage3.alice.core.property.ValueProperty;
import edu.cmu.cs.stage3.lang.Messages;

public class Variable extends Expression {
	public final ValueProperty value = new ValueProperty( this, "value", null ); 
	public final ClassProperty valueClass = new ClassProperty( this, "valueClass", null ); 
	public boolean isWatch = false;
	
	public Object getValue() {
		return value.getValue();
	}
	
	public Class getValueClass() {
		return (Class)valueClass.getValue();
	}
	//todo
	protected void valueClassValueChanging( Class clsToBe ) {
		if( clsToBe!=null ) {
			Object o = value.getValue();
			if( o instanceof Expression ) {
				Expression expression = (Expression)o;
				if( clsToBe.isAssignableFrom( expression.getValueClass() ) ) {
					value.setOverrideValueClass( clsToBe );
				} else {
					if( Element.s_isLoading ) {
						//pass
					} else {
						throw new RuntimeException( Messages.getString("cannot_change_valueClass_to_when_value_is_", clsToBe, o) );  
					}
				}
			} else {
				if( o==null || clsToBe.isInstance( o ) ) {
					value.setOverrideValueClass( clsToBe );
				} else {
					if( Element.s_isLoading ) {
						//pass
					} else {
						throw new RuntimeException( Messages.getString("cannot_change_valueClass_to_when_value_is_", clsToBe, o) );  
					}
				}
			}
		} else {
			value.setOverrideValueClass( null );
		}
	}
	//todo
	protected void valueValueChanging( Object o ) {
		if( Element.s_isLoading ) {
			//pass
		} else {
			if( o==null ) {
				//pass
			} else {
				if( o instanceof Expression ) {
					Expression expression = (Expression)o;
					Class cls = expression.getValueClass();
					Class valueCls = (Class)valueClass.getValue();
					if( valueCls==null ) {
						//pass
					} else {
						if( valueCls.isAssignableFrom( cls ) ) {
							//pass
						} else {
							throw new RuntimeException( Messages.getString("is_not_an_instance_of____it_is_an_instance_of_", o, valueClass, cls) );  
						}
					}
	
				} else {
					Class cls = o.getClass();
					Class valueCls = (Class)valueClass.getValue();
					if( valueCls==null ) {
						//pass
					} else {
						if( valueCls.isAssignableFrom( cls ) ) {
							//pass
						} else {
							throw new RuntimeException( Messages.getString("is_not_an_instance_of____it_is_an_instance_of_", o, valueClass, cls) );  
						}
					}
				}
			}
		}
	}
	protected void valueValueChanged( Object o ) {
		onExpressionChange();
	}

	
	protected void propertyChanging( Property property, Object o ) {
		if( property == value ) {
			valueValueChanging( o );
		} else if( property == valueClass ) {
			valueClassValueChanging( (Class)o );
		} else {
			super.propertyChanging( property, o );
		}
	}
	
	protected void propertyChanged( Property property, Object o ) {
		if( property == value ) {
			valueValueChanged( o );
		} else if( property == valueClass ) {
			//pass
		} else {
			super.propertyChanged( property, o );
		}
	}
}
