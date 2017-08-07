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
package actor4j.core.utils;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import actor4j.core.ActorSystem;
import actor4j.core.actors.Actor;
import actor4j.core.messages.FutureActorMessage;
import actor4j.core.pattern.actors.FutureActor;

public final class FuturePattern {
	public static <T> Future<T> ask(T value, int tag, UUID dest, Actor actor) {
		UUID mediator = actor.getSystem().addActor(() -> new FutureActor(dest, true));
		
		return ask(value, tag, dest, mediator, actor);
	}
	
	public static <T> Future<T> ask(T value, int tag, UUID dest, UUID mediator, Actor actor) {	
		CompletableFuture<T> result = new CompletableFuture<>();
		actor.send(new FutureActorMessage<T>(result, value, tag, actor.self(), mediator));
		
		return result;
	}
	
	public static <T> Future<T> ask(T value, int tag, UUID dest, ActorSystem system) {
		UUID mediator = system.addActor(() -> new FutureActor(dest, true));
		
		return ask(value, tag, dest, mediator, system);
	}
	
	public static <T> Future<T> ask(T value, int tag, UUID dest, UUID mediator, ActorSystem system) {	
		CompletableFuture<T> result = new CompletableFuture<>();
		system.send(new FutureActorMessage<T>(result, value, tag, system.SYSTEM_ID, mediator));
		
		return result;
	}
}
