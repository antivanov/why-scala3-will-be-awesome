package scala2.domain

object DefinitionOne {
  final case class Username private (value: String)

  object Username {

    def fromString(v: String): Option[Username] =
      if (v.matches("^@[a-z0-9]{4,32}$")) Some(new Username(v)) else None

    def upper(username: Username): Username =
      username.copy(value = username.value.toUpperCase)

    val johndoe: Username = new Username("@johndoe")
  }
}

object ExampleA extends App {
  import DefinitionOne._

  val userA = Username.fromString("foo")
  val userB = Username.fromString("@foobar")

  println(s"userA = $userA")
  println(s"userB = $userB")

  // primary constructor not available - as expected
  //val userC = new Username("foo")

  // unfortunately autogenerated `apply` and `copy`
  // can be used to create invalid values :(
  val userD = Username.apply("foo")
  val userE = Username.johndoe.copy(value = "foo")

  println(s"userD = $userD")
  println(s"userE = $userE")
}

// -----------------------------------------------------

object DefinitionTwo {
  sealed abstract case class Username private (value: String)

  object Username {

    def fromString(v: String): Option[Username] =
      if (v.matches("^@[a-z0-9]{4,32}$")) Some(new Username(v) {}) else None

    def upper(username: Username): Username =
       new Username(username.value.toUpperCase) {}
    //   username.copy(value = username.value.toUpperCase)
  }
}

object ExampleAFix extends App {
  import DefinitionTwo._
  
  val userA = Username.fromString("foo")
  val userB = Username.fromString("@foobar")

  println(s"userA = $userA")
  println(s"userB = $userB")

  // blocked by abstract modified - no longer generated
  // val userC = Username.apply("foo")
  // val userD = userB.get.copy(value = "foo")
}

// -----------------------------------------------------

object ExampleB extends App {

  import java.util.UUID
  import scala.util.Try

  sealed abstract case class Email private (value: String)
  object Email {
    def fromString(v: String): Option[Email] =
      if (isValidEmail(v)) Some(new Email(v) {}) else None

    def isValidEmail(v: String): Boolean =
      v.matches("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$")
  }

  sealed abstract case class UserId private (value: UUID)
  object UserId {
    def fromString(v: String): Option[UserId] =
      Try(UUID.fromString(v)).toOption.map(new UserId(_) {})

    def makeRandom: UserId = new UserId(UUID.randomUUID) {}
  }

  sealed trait UserCommand
  final case class SignUp(email: Email)                       extends UserCommand
  final case class AddFriend(id: UserId, friendId: UserId)    extends UserCommand
  final case class RemoveFriend(id: UserId, friendId: UserId) extends UserCommand

  sealed trait UserEvent { def userId: UserId }
  final case class UserCreated(userId: UserId, email: Email)       extends UserEvent
  final case class FriendAdded(userId: UserId, friendId: UserId)   extends UserEvent
  final case class FriendRemoved(userId: UserId, friendId: UserId) extends UserEvent
}