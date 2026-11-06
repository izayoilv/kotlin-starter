package com.example.mahasiswaapi

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object Database {
  private val url = "jdbc:sqlite:" + (System.getenv("MAHASISWA_DB") ?: "mahasiswa.db")

  init {
    Class.forName("org.sqlite.JDBC")
    connection().use { conn ->
      conn.createStatement().use { it.execute("PRAGMA journal_mode = WAL") }
      conn.createStatement().use { statement ->
        statement.executeUpdate(
          """
          CREATE TABLE IF NOT EXISTS mahasiswa (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nim TEXT NOT NULL UNIQUE,
            nama TEXT NOT NULL,
            jurusan TEXT,
            email TEXT
          )
          """.trimIndent(),
        )
      }
      val count =
        conn.createStatement().use { statement ->
          statement.executeQuery("SELECT COUNT(*) FROM mahasiswa").use { rows ->
            if (rows.next()) rows.getInt(1) else 0
          }
        }
      if (count == 0) seed(conn)
    }
  }

  private fun connection(): Connection =
    DriverManager.getConnection(url).apply {
      createStatement().use { it.execute("PRAGMA busy_timeout = 5000") }
    }

  private fun seed(conn: Connection) {
    listOf(
      Mahasiswa(nim = "20230001", nama = "Ayu Lestari", jurusan = "Informatika", email = "ayu@example.com"),
      Mahasiswa(nim = "20230002", nama = "Budi Santoso", jurusan = "Sistem Informasi", email = "budi@example.com"),
      Mahasiswa(nim = "20230003", nama = "Citra Dewi", jurusan = "Teknik Komputer", email = "citra@example.com"),
    ).forEach { insert(conn, it) }
  }

  fun all(): List<Mahasiswa> =
    connection().use { conn ->
      conn.prepareStatement("SELECT id, nim, nama, jurusan, email FROM mahasiswa ORDER BY id DESC").use { ps ->
        ps.executeQuery().use { rows ->
          buildList {
            while (rows.next()) {
              add(
                Mahasiswa(
                  id = rows.getInt("id"),
                  nim = rows.getString("nim"),
                  nama = rows.getString("nama"),
                  jurusan = rows.getString("jurusan").orEmpty(),
                  email = rows.getString("email").orEmpty(),
                ),
              )
            }
          }
        }
      }
    }

  fun create(mahasiswa: Mahasiswa): DbResult {
    connection().use { conn ->
      if (nimExists(conn, mahasiswa.nim, null)) return DbResult.Error("NIM sudah terdaftar")
      try {
        insert(conn, mahasiswa)
      } catch (e: SQLException) {
        if (e.isUniqueViolation()) return DbResult.Error("NIM sudah terdaftar")
        throw e
      }
    }
    return DbResult.Success("Data berhasil ditambahkan")
  }

  fun update(mahasiswa: Mahasiswa): DbResult {
    connection().use { conn ->
      if (!existsById(conn, mahasiswa.id)) return DbResult.Error("Data tidak ditemukan")
      if (nimExists(conn, mahasiswa.nim, mahasiswa.id)) {
        return DbResult.Error("NIM sudah digunakan mahasiswa lain")
      }
      try {
        conn
          .prepareStatement(
            "UPDATE mahasiswa SET nim = ?, nama = ?, jurusan = ?, email = ? WHERE id = ?",
          ).use { ps ->
            ps.setString(1, mahasiswa.nim)
            ps.setString(2, mahasiswa.nama)
            ps.setString(3, mahasiswa.jurusan)
            ps.setString(4, mahasiswa.email)
            ps.setInt(5, mahasiswa.id)
            ps.executeUpdate()
          }
      } catch (e: SQLException) {
        if (e.isUniqueViolation()) return DbResult.Error("NIM sudah digunakan mahasiswa lain")
        throw e
      }
    }
    return DbResult.Success("Data berhasil diupdate")
  }

  fun delete(id: Int): DbResult {
    connection().use { conn ->
      if (!existsById(conn, id)) return DbResult.Error("Data tidak ditemukan")
      conn.prepareStatement("DELETE FROM mahasiswa WHERE id = ?").use { ps ->
        ps.setInt(1, id)
        ps.executeUpdate()
      }
    }
    return DbResult.Success("Data berhasil dihapus")
  }

  private fun insert(
    conn: Connection,
    mahasiswa: Mahasiswa,
  ) {
    conn
      .prepareStatement(
        "INSERT INTO mahasiswa (nim, nama, jurusan, email) VALUES (?, ?, ?, ?)",
      ).use { ps ->
        ps.setString(1, mahasiswa.nim)
        ps.setString(2, mahasiswa.nama)
        ps.setString(3, mahasiswa.jurusan)
        ps.setString(4, mahasiswa.email)
        ps.executeUpdate()
      }
  }

  private fun nimExists(
    conn: Connection,
    nim: String,
    excludeId: Int?,
  ): Boolean {
    val sql =
      if (excludeId == null) {
        "SELECT 1 FROM mahasiswa WHERE nim = ?"
      } else {
        "SELECT 1 FROM mahasiswa WHERE nim = ? AND id != ?"
      }
    return conn.prepareStatement(sql).use { ps ->
      ps.setString(1, nim)
      if (excludeId != null) ps.setInt(2, excludeId)
      ps.executeQuery().use { it.next() }
    }
  }

  private fun existsById(
    conn: Connection,
    id: Int,
  ): Boolean =
    conn.prepareStatement("SELECT 1 FROM mahasiswa WHERE id = ?").use { ps ->
      ps.setInt(1, id)
      ps.executeQuery().use { it.next() }
    }

  private fun SQLException.isUniqueViolation(): Boolean = message?.contains("UNIQUE", ignoreCase = true) == true
}
