package org.openmrs.module.sms.api.handler;

/**
 * The IncomingMessageHandler Interface.
 *
 * <p>The implementation of this interface handles messages received by the system. The system
 * collects all beans which implement the IncomingMessageHandler, sorts them according to their
 * priorities and executes 'handle' methods for each in order - the execution 'chain' may be stopped
 * if proceeding handler's 'handle' method returns 'true' - that denotes a message to be handled,
 * and prevents any following handler from receiving the message.
 *
 * <p>The go-to use case for IncomingMessageHandler is sending an automatic response.
 *
 * @implSpec Each implementation must be a Spring bean.
 */
public interface IncomingMessageHandler {
  int BUILD_IN_PRIORITY = 10;

  /**
   * Used to determine an order of execution when multiple beans implementing the
   * IncomingMessageHandler are found.
   *
   * <p>The beans are sorted <b>from high to low value</b>.
   *
   * @return the priority of this specific implementation
   * @implNote The priority of a build-in implementation is defined by {@link #BUILD_IN_PRIORITY},
   *     implementor are free to reference it to position their beans.
   */
  int priority();

  /**
   * Handle the received message.
   *
   * @return true, if no further Message Handler should be called to process this specific message,
   *     false otherwise
   * @throws org.openmrs.api.APIException if the handling of message failed
   * @implSpec All exceptions must be wrapped in APIException. The implementor is responsible for
   *     creating a new transaction, or making sure that failure in this method call won't break the
   *     caller's transaction.
   */
  boolean handle(IncomingMessageData message);
}
