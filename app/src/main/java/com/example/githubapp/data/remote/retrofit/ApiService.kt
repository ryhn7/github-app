package com.example.githubapp.data.remote.retrofit

import com.example.githubapp.data.remote.response.DataUser
import com.example.githubapp.data.remote.response.GithubUserResponse
import com.example.githubapp.data.remote.response.User
import retrofit2.http.*

interface ApiService {

    @GET("search/users")
    suspend fun searchUsername(
        @Query("q") q: String
    ): GithubUserResponse

    @GET("users/{username}")
    suspend fun getUserDetailByUsername(
        @Path("username") username: String
    ): DataUser

    @GET("users/{username}/followers")
    suspend fun getUserFollowers(
        @Path("username") username: String
    ): ArrayList<User>

    @GET("users/{username}/following")
    suspend fun getUserFollowing(
        @Path("username") username: String
    ): ArrayList<User>
}