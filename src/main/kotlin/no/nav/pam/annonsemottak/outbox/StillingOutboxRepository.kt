package no.nav.pam.annonsemottak.outbox

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp
import java.time.LocalDateTime

@Repository
open class StillingOutboxRepository(@Autowired jdbcTemplate: JdbcTemplate) {
    private val namedJdbcTemplate = NamedParameterJdbcTemplate(jdbcTemplate)

    private val rowMapper = RowMapper { rs, _ ->
        StillingOutbox(
            id = rs.getLong("id"),
            uuid = rs.getString("uuid"),
            payload = rs.getString("payload"),
            opprettetDato = rs.getObject("opprettet_dato", LocalDateTime::class.java),
            harFeilet = rs.getBoolean("har_feilet"),
            antallForsøk = rs.getInt("antall_forsok"),
            sisteForsøkDato = rs.getObject("siste_forsok_dato", LocalDateTime::class.java),
            prosessertDato = rs.getObject("prosessert_dato", LocalDateTime::class.java)
        )
    }

    @Transactional
    open fun lagre(entity: StillingOutbox): Int {
        val sql = """
            INSERT INTO stilling_outbox (uuid, payload, opprettet_dato, har_feilet, antall_forsok, siste_forsok_dato, prosessert_dato)
            VALUES (:uuid, :payload, :opprettet_dato, :har_feilet, :antall_forsok, :siste_forsok_dato, :prosessert_dato)
        """.trimIndent()

        val params = MapSqlParameterSource(
            mapOf(
                "uuid" to entity.uuid,
                "payload" to entity.payload,
                "opprettet_dato" to entity.opprettetDato,
                "har_feilet" to entity.harFeilet,
                "antall_forsok" to entity.antallForsøk,
                "siste_forsok_dato" to entity.sisteForsøkDato,
                "prosessert_dato" to entity.prosessertDato
            )
        )

        return namedJdbcTemplate.update(sql, params)
    }


    @Transactional
    open fun hentUprosesserteMeldinger(batchSize: Int = 1000, outboxDelay: Long = 30): List<StillingOutbox> {
        val sql = """
            SELECT id, uuid, payload, opprettet_dato, har_feilet, antall_forsok, siste_forsok_dato, prosessert_dato 
            FROM stilling_outbox
            WHERE prosessert_dato is null AND opprettet_dato <= :cutoff
            ORDER BY opprettet_dato ASC
            LIMIT :limit
        """.trimIndent()
        val params = MapSqlParameterSource(mapOf("cutoff" to LocalDateTime.now().minusSeconds(outboxDelay), "limit" to batchSize))

        return namedJdbcTemplate.query(sql, params, rowMapper)
    }

    @Transactional
    open fun lagreFlere(entities: Iterable<StillingOutbox>) = entities.sumOf { lagre(it) }

    @Transactional
    open fun markerSomProsessert(stillingOutbox: StillingOutbox): Boolean {
        val sql = """UPDATE stilling_outbox SET prosessert_dato = :dato WHERE id = :id"""
        val params = MapSqlParameterSource(mapOf("dato" to stillingOutbox.prosessertDato?.toTimeStamp(), "id" to stillingOutbox.id!!))

        return namedJdbcTemplate.update(sql, params) > 0
    }

    @Transactional
    open fun markerSomFeilet(stillingOutbox: StillingOutbox): Boolean {
        val sql = """UPDATE stilling_outbox SET har_feilet = :harFeilet, antall_forsok = :antallForsok, siste_forsok_dato = :sisteForsokDato WHERE id = :id"""
        val params = MapSqlParameterSource(mapOf(
            "harFeilet" to stillingOutbox.harFeilet,
            "antallForsok" to stillingOutbox.antallForsøk,
            "sisteForsokDato" to stillingOutbox.sisteForsøkDato?.toTimeStamp(),
            "id" to stillingOutbox.id!!
        ))

        return namedJdbcTemplate.update(sql, params) > 0
    }

    @Transactional
    open fun hentAlle() = namedJdbcTemplate.query("SELECT * FROM stilling_outbox", rowMapper)

    private fun LocalDateTime.toTimeStamp(): Timestamp {
        return Timestamp.valueOf(this)
    }
}
