/*
 * Copyright (c) 2015-2018, David A. Bauer. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package actor4j.core.immutable;

import actor4j.core.messages.ActorMessage;
import actor4j.core.utils.Copyable;
import actor4j.core.utils.Shareable;

public class ImmutableObject<T> implements Shareable {
	protected final T value;
	
	public ImmutableObject(T value) {
		super();
		
		if (value!=null)
			if (!(ActorMessage.isSupportedType(value.getClass()) || value instanceof Shareable || value instanceof Copyable || value instanceof Exception))
				throw new IllegalArgumentException();
		
		this.value = value;
	}
	
	public T get() {
		return value;
	}
}
