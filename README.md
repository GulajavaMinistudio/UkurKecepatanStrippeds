# Ukur Kecepatan (Stripped Code)
Aplikasi Ukur Kecepatan adalah aplikasi untuk mengukur prakiraan kecepatan kendaraan yang anda naiki. Aplikasi ini mengukur kecepatan kendaraan yang anda naiki dengan bantuan kordinat GPS dan kordinat jaringan seluler atau jaringan WiFi yang digunakan oleh perangkat telepon atau tablet anda. Aplikasi akan menampilkan 3 tipe kecepatan yaitu kilometer/jam (kph) , mil/jam (mph) , dan knot (knt) .

Aplikasi ini telah dikurangi beberapa bagian asetnya karena, seperti aset gambar dan logo. Dikurangi menjadi hanya menggunakan gambar sederhana saja untuk mengisi aset gambar di dalam aplikasi. Karena aplikasi ini hanya ditujukan untuk kode sumber pembelajaran saja dan contoh kode saja.


# Perlu Google Play Services!
Aplikasi ini membutuhkan Google Play Services Location untuk menggunakan API Google Location Services. Oleh karena itu, aplikasi ini membutuhkan file google-services.json yang dibuat dengan bantuan [Firebase Console][firebase-playservices]. Bantuannya bisa dilihat [Google Firebase Help Center][firebase-playservices-help]

# Demo Aplikasi Versi Produksi untuk Pengukur Kecepatan
Demo aplikasi Ukur Kecepatan yang telah ada di Google Play Store dengan basis kode yang sama dapat dilihat di [Google Play Store][app-produksi]

## Aplikasi ini menggunakan beberapa library, diantaranya :
  - Android Support Library v4 dan v7
  - Android Design Library
  - Android Architecture Component (Lifecycle, MVVM, dst)
  - Fast Android Networking (FAN) dan OkHTTP

[firebase-playservices]: <https://firebase.google.com/>
 [firebase-playservices-help]: <https://support.google.com/firebase/answer/7015592?hl=en/>
[app-produksi]:<https://play.google.com/store/apps/details?id=gulajava.speedcepat/>
