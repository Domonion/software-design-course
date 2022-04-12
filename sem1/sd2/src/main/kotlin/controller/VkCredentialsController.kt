package controller

import util.IoUtil
import vk.IVkCredentials

class VkCredentialsController : IVkCredentials, IController {
    override val apiVersion = "5.131"
    override val apiUrl = "https://api.vk.com/method/"
    override val accessToken: String = IoUtil.ask("Provide access token: ", IoUtil.alwaysTrue())
}