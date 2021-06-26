import com.helloworldramen.kingoyster.entities.ActorFactory

fun main() {
    val entity = ActorFactory.player()()
    val serialization = entity.serialize(shouldPrettyPrint = true)
//    val entityCopy = Gson().fromJson(serialization, Entity::class.java)

    println(serialization)

}