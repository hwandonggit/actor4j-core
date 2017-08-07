/*
 * Copyright (c) 2015-2017, David A. Bauer. All rights reserved.
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
package actor4j.core.protocols;

import actor4j.core.ActorCell;
import actor4j.core.actors.PersistentActor;
import actor4j.core.messages.ActorMessage;
import actor4j.core.persistence.actor.PersistenceServiceActor;

public class RecoverProtocol {
	protected final ActorCell cell;

	public RecoverProtocol(ActorCell cell) {
		this.cell = cell;
	}
	
	public void apply() {
		if (cell.getSystem().isPersistenceMode() && cell.getActor() instanceof PersistentActor) {
			cell.setActive(false);
			cell.getSystem().getMessageDispatcher().postPersistence(
					new ActorMessage<String>(((PersistentActor<?,?>)cell.getActor()).persistenceId().toString(), PersistenceServiceActor.RECOVER, cell.getId(), null));
		}
	}
}
