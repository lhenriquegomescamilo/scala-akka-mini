package com.camilo
package com.camilo.actos

import akka.actor.typed.{ActorRef, Behavior}
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior}


// single bank account
class PersistentBankAccount {
  /**
   * ### Reason to use event source
   * - fault tolerance
   * - auditing
   * -
   */

  // command = messages
  sealed trait Command

  case class CreateBankAccount(user: String, current: String, initialBalance: Double, replyTo: ActorRef[Response]) extends Command // do not use double in the real application

  case class UpdateBalance(id: String, currency: String, amount: Double /*this  can be negative <0 */ , replyTo: ActorRef[Response]) extends Command

  case class GetBankAccount(id: String, replyTo: ActorRef[Response]) extends Command

  // event = to persist to cassandra

  trait Event

  case class BankAccountCreated(bankAccount: BankAccount) extends Event

  case class BalanceUpdate(amount: Double) extends Event

  // state
  trait State

  case class BankAccount(id: String, user: String, currency: String, balance: Double) extends State
  // response

  sealed trait Response

  case class BankAccountCreateResponse(id: String) extends Response

  case class BankAccountBalanceUpdatedResponse(maybeBankAccount: Option[BankAccount]) extends Response

  case class GetBankAccountResponse(maybeBankAccount: Option[BankAccount]) extends Response

  // command handle = message handle => persist an event
  // event handle => update state
  // state

  /**
   * - Bank created me
   * - bank send me CreateBankAccount
   * - I persist BankAccountCreated
   * - I update my state
   * - reply back to bank with the BankAccountCreatedResponse
   * - (the bank surfaces the response to the HttpServer)
   *
   */
  val commandHandler: (BankAccount, Command) => Effect[Event, BankAccount] = (state, command) => command match {

    case CreateBankAccount(user, currency, initialBalance, bank) =>
      val id = state.id
      Effect.persist(BankAccountCreated(BankAccount(id, user, currency, initialBalance))) // this is persisted into Cassandra
        .thenReply(bank)(_ => BankAccountCreateResponse(id))


    case UpdateBalance(_, _, amount, replyTo) =>
      val newBalance = state.balance + amount
      // this is illegal
      if (newBalance < 0) Effect.reply(replyTo)(BankAccountBalanceUpdatedResponse(None))
      else Effect.persist(BalanceUpdate(newBalance)).thenReply(replyTo)(newState => BankAccountBalanceUpdatedResponse(Some(newState)))

    case GetBankAccount(_, replyTo) => Effect.reply(replyTo)(GetBankAccountResponse(Some(state)))
  }

  val eventHandler: (BankAccount, Event) => BankAccount = (state, event) => event match {
    case BankAccountCreated(bankAccount) => bankAccount
    case BalanceUpdate(amount) => state.copy(balance = state.balance + amount)
  }

  def apply(id: String): Behavior[Command] =
    EventSourcedBehavior[Command, Event, BankAccount](
      persistenceId = PersistenceId.ofUniqueId(id),
      emptyState = BankAccount(id, "", "", 0.0), // unused,
      commandHandler = commandHandler,
      eventHandler = eventHandler
    )

}
