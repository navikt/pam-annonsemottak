package no.nav.pam.annonsemottak.receivers.amedia

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.pam.annonsemottak.receivers.HttpClientProvider
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.IOException
import javax.inject.Named

private val log = LoggerFactory.getLogger(AmediaConnector::class.java)

@Component
internal class AmediaConnector(
        @Named("proxyHttpClient") private val clientProvider: HttpClientProvider,
        @Value("\${amedia.url}") private val amediaUrl: String,
        private val jacksonMapper: ObjectMapper) {

    fun hentData(parameters: AmediaRequestParametere) = (client call parameters.asUrl)
            .apply { require(isSuccessful) { "Unexpected response code " + code() } }
            .let { jacksonMapper.readValue(it.body().charStream(), JsonNode::class.java) }

    fun isPingSuccessful() = try { (client call pingurl).isSuccessful } catch (e: IOException) { false }

    
    private val AmediaRequestParametere.asUrl get() = amediaUrl + this.asString()

    private val pingurl = AmediaRequestParametere.PING.asUrl

    private val client: OkHttpClient get() = clientProvider.get()

    private infix fun OkHttpClient.call(url: String) = newCall(url.request).execute()

    private val String.request get() = Request.Builder().url(this).build()
            .apply { log.debug("{}", this) }
}

