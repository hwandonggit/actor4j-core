/*
 * Copyright (c) 2015, David A. Bauer
 */
package actor4j.core;

import static actor4.core.utils.ActorLogger.logger;
import static actor4.core.utils.ActorUtils.actorLabel;
import static actor4j.core.ActorProtocolTag.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import actor4j.core.messages.ActorMessage;
import actor4j.function.Consumer;

public class StopProtocol {
	protected final Actor actor;

	public StopProtocol(Actor actor) {
		this.actor = actor;
	}
	
	protected void postStop() {
		actor.postStop();
		actor.internal_stop();
		logger().info(String.format("%s - System: actor (%s) stopped", actor.system.name, actorLabel(actor)));
	}
	
	public void apply() {
		final List<UUID> waitForChildren =new ArrayList<>(actor.getChildren().size());
		
		Iterator<UUID> iterator = actor.getChildren().iterator();
		while (iterator.hasNext()) {
			UUID dest = iterator.next();
			actor.watch(dest);
		}
		iterator = actor.getChildren().iterator();
		while (iterator.hasNext()) {
			UUID dest = iterator.next();
			waitForChildren.add(dest);
			actor.watch(dest);
			actor.getSystem().sendAsDirective(new ActorMessage<>(null, INTERNAL_STOP, actor.getSelf(), dest));
		}
		
		if (waitForChildren.isEmpty()) 
			postStop();
		else
			actor.become(new Consumer<ActorMessage<?>>() {
				@Override
				public void accept(ActorMessage<?> message) {
					if (message.tag==INTERNAL_STOP_SUCCESS) {
						waitForChildren.remove(message.source);
						if (waitForChildren.isEmpty())
							postStop();
					}
				}
			});
	}
}
