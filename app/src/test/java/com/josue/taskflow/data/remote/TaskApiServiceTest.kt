package com.josue.taskflow.data.remote

import com.josue.taskflow.data.remote.api.TaskApiService
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class TaskApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: TaskApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val json = Json { ignoreUnknownKeys = true }
        val contentType = "application/json".toMediaType()

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(TaskApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getTodos con respuesta 200 deserializa correctamente la lista de TaskDto`() = runBlocking {
        val jsonResponse = """
            [
                {"userId": 1, "id": 1, "title": "delectus aut autem", "completed": false},
                {"userId": 1, "id": 2, "title": "quis ut nam facilis et officia qui", "completed": true}
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val result = apiService.getTodos(userId = 1)

        assertEquals(2, result.size)
        assertEquals(1, result[0].id)
        assertEquals("delectus aut autem", result[0].title)
        assertFalse(result[0].completed)
        assertTrue(result[1].completed)

        val request = mockWebServer.takeRequest()
        assertEquals("/todos?userId=1", request.path)
    }

    @Test(expected = HttpException::class)
    fun `getTodos con respuesta HTTP 500 lanza HttpException`(): Unit = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Server Error")
        )

        apiService.getTodos(userId = 1)
    }
}
