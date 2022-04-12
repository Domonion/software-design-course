import controller.HashtagController
import controller.MainController
import controller.VkCredentialsController
import org.kodein.di.*
import vk.VkApi
import vk.VkTranslator
import vk.VkEngine

object Application {
    val kodein = DI {
        bind { singleton { VkCredentialsController() } }
        bind { singleton { VkTranslator() } }
        bind { singleton { VkEngine(instance()) } }
        bind { singleton { VkApi(instance(), instance()) } }
        bind { singleton { HashtagController(instance()) } }
        bind { singleton { MainController(instance()) } }
    }
}