package vk

import api.ICredentials

interface IVkCredentials : ICredentials {
    val apiVersion: String
    val accessToken: String
    val apiUrl: String
}