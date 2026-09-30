package com.example.newsreader

object ArticleData {
  val articles: List<Article> = listOf(
    Article(
      id = 1,
      title = "Kurikulum Pemrograman Mobile Diperbarui",
      snippet = "Silabus baru menambahkan materi Jetpack dan navigasi antar Fragment.",
      body = "Program studi memperbarui silabus pemrograman mobile untuk semester ini. " +
        "Materi baru mencakup Fragment, Navigation Component, dan praktik terbaik " +
        "arsitektur aplikasi Android modern. Mahasiswa diharapkan menyelesaikan " +
        "satu proyek kecil untuk setiap bab.",
    ),
    Article(
      id = 2,
      title = "Bottom Navigation Kembali Populer",
      snippet = "Pola navigasi bawah kembali menjadi standar aplikasi berita.",
      body = "Banyak aplikasi berita besar kembali menggunakan navigasi bawah " +
        "karena jangkauan jempol yang nyaman. Material Design menyediakan " +
        "BottomNavigationView yang terintegrasi langsung dengan Navigation Component.",
    ),
    Article(
      id = 3,
      title = "Fragment vs Activity: Kapan Memilih?",
      snippet = "Fragment lebih ringan dan reusable, Activity lebih mandiri.",
      body = "Fragment cocok untuk UI modular yang dipakai ulang di beberapa layar, " +
        "sementara Activity cocok sebagai titik masuk aplikasi. Kombinasi " +
        "Activity tunggal dengan banyak Fragment adalah pola yang umum saat ini.",
    ),
    Article(
      id = 4,
      title = "Tips Mengelola Back Stack Navigasi",
      snippet = "Biarkan NavController mengurus back stack; jangan kelola manual.",
      body = "NavController menangani tombol back secara otomatis sesuai NavGraph. " +
        "Menambahkan action pada graph membuat alur navigasi eksplisit dan " +
        "mudah dilacak dibandingkan transaksi manual.",
    ),
    Article(
      id = 5,
      title = "Transisi Animasi Antar Halaman",
      snippet = "Animasi sederhana membuat navigasi terasa lebih halus.",
      body = "Navigation Component mendukung animasi transisi antar tujuan. " +
        "Meski opsional, animasi masuk dan keluar yang halus meningkatkan " +
        "pengalaman pengguna tanpa menambah kode yang rumit.",
    ),
  )

  fun byId(id: Int): Article? = articles.firstOrNull { it.id == id }
}
