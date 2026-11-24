package com.example.lokalshop.controller;

import com.example.lokalshop.model.*;
import com.example.lokalshop.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class LokalShopController {

    private final BarangRepository barangRepo;
    private final CodPointRepository codPointRepo;
    private final PenggunaRepository penggunaRepo;
    private final TransaksiRepository transaksiRepo;
    private final SuperBoostRepository boostRepo;
    private final PembayaranRepository pembayaranRepo;
    private final UlasanRepository ulasanRepo;

    public LokalShopController(
            BarangRepository barangRepo,
            CodPointRepository codPointRepo,
            PenggunaRepository penggunaRepo,
            TransaksiRepository transaksiRepo,
            SuperBoostRepository boostRepo,
            PembayaranRepository pembayaranRepo,
            UlasanRepository ulasanRepo) {
        this.barangRepo = barangRepo;
        this.codPointRepo = codPointRepo;
        this.penggunaRepo = penggunaRepo;
        this.transaksiRepo = transaksiRepo;
        this.boostRepo = boostRepo;
        this.pembayaranRepo = pembayaranRepo;
        this.ulasanRepo = ulasanRepo;
    }

    // ==========================================
    // BAGIAN AUTENTIKASI (LOGIN, REGISTER, LOGOUT)
    // ==========================================

    @GetMapping("/login")
    public String halamanLogin(HttpSession session) {
        if (session.getAttribute("userLogin") != null) {
            return "redirect:/";
        }
        return "login";
    }

    @PostMapping("/login")
    public String prosesLogin(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttrs) {

        Optional<Pengguna> userOpt = penggunaRepo.findByEmail(email);
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            session.setAttribute("userLogin", userOpt.get());
            return "redirect:/";
        }
        redirectAttrs.addFlashAttribute("error", "Email atau password salah!");
        return "redirect:/login";
    }

    @PostMapping("/register")
    public String prosesRegister(
            @RequestParam String nama,
            @RequestParam String email,
            @RequestParam String nomorTelepon,
            @RequestParam String password,
            @RequestParam String koordinatLokasi,
            HttpSession session,
            RedirectAttributes redirectAttrs) {

        if (penggunaRepo.findByEmail(email).isPresent()) {
            redirectAttrs.addFlashAttribute("error", "Email sudah terdaftar, silakan langsung masuk.");
            return "redirect:/login";
        }

        Pengguna baru = new Pengguna();
        baru.setNama(nama);
        baru.setEmail(email);
        baru.setNomorTelepon(nomorTelepon);
        baru.setPassword(password);
        baru.setKoordinatLokasi(koordinatLokasi);
        Pengguna saved = penggunaRepo.save(baru);

        session.setAttribute("userLogin", saved);
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String prosesLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // ==========================================
    // BAGIAN KATALOG & LISTING BARANG
    // ==========================================

    @GetMapping("/")
    public String halamanUtama(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String kondisi,
            HttpSession session,
            Model model) {

        // Boleh null jika pengunjung belum login (Guest Mode)
        Pengguna userAktif = (Pengguna) session.getAttribute("userLogin");

        // Hanya tampilkan barang berstatus "Tersedia"
        List<Barang> daftarBarang = barangRepo.findByStatusListing("Tersedia");

        // Filter berdasarkan pencarian nama barang atau kategori
        if (keyword != null && !keyword.isEmpty()) {
            String kataKunci = keyword.toLowerCase();
            daftarBarang = daftarBarang.stream()
                    .filter(b -> (b.getNamaBarang() != null && b.getNamaBarang().toLowerCase().contains(kataKunci)) ||
                                 (b.getDeskripsi() != null && b.getDeskripsi().toLowerCase().contains(kataKunci)))
                    .collect(Collectors.toList());
        }

        // Filter berdasarkan kondisi barang (Baru / Preloved)
        if (kondisi != null && !kondisi.isEmpty()) {
            daftarBarang = daftarBarang.stream()
                    .filter(b -> b.getKondisiBarang() != null && b.getKondisiBarang().equalsIgnoreCase(kondisi))
                    .collect(Collectors.toList());
        }

        // Cek barang dengan Super Boost Aktif
        Set<Integer> boostedBarangIds = boostRepo.findByStatusBoost("Aktif").stream()
                .map(b -> b.getBarang().getIdBarang())
                .collect(Collectors.toSet());

        for (Barang b : daftarBarang) {
            if (boostedBarangIds.contains(b.getIdBarang())) {
                b.setIsBoosted(true);
            }
        }

        // Urutkan Super Boost HOT di paling atas, reguler terbaru di bawahnya
        daftarBarang.sort((b1, b2) -> {
            if (b1.getIsBoosted() != b2.getIsBoosted()) {
                return b1.getIsBoosted() ? -1 : 1;
            }
            return b2.getIdBarang().compareTo(b1.getIdBarang());
        });

        // Hitung rata-rata rating per penjual
        Map<Integer, String> ratingPenjualMap = new HashMap<>();
        List<Ulasan> semuaUlasan = ulasanRepo.findAll();
        Map<Integer, List<Ulasan>> ulasanPerPenjual = semuaUlasan.stream()
                .collect(Collectors.groupingBy(u -> u.getPenjual().getIdPengguna()));

        for (Map.Entry<Integer, List<Ulasan>> entry : ulasanPerPenjual.entrySet()) {
            double avg = entry.getValue().stream().mapToInt(Ulasan::getRating).average().orElse(0.0);
            ratingPenjualMap.put(entry.getKey(), String.format(Locale.US, "%.1f (%d)", avg, entry.getValue().size()));
        }

        model.addAttribute("userAktif", userAktif);
        model.addAttribute("daftarBarang", daftarBarang);
        model.addAttribute("ratingPenjualMap", ratingPenjualMap);
        return "index";
    }

    @GetMapping("/barang/tambah")
    public String formTambahBarang(HttpSession session, Model model) {
        Pengguna userAktif = (Pengguna) session.getAttribute("userLogin");
        if (userAktif == null) return "redirect:/login";
        model.addAttribute("userAktif", userAktif);
        return "tambah-barang";
    }

    @PostMapping("/barang/simpan")
    public String simpanBarang(
            @RequestParam String namaBarang,
            @RequestParam String kategori,
            @RequestParam BigDecimal harga,
            @RequestParam String kondisiBarang,
            @RequestParam String deskripsi,
            @RequestParam MultipartFile fotoFile,
            @RequestParam(required = false) Boolean aktifkanBoost,
            HttpSession session,
            RedirectAttributes redirectAttrs) {

        Pengguna penjual = (Pengguna) session.getAttribute("userLogin");
        if (penjual == null) return "redirect:/login";

        // Validasi backend: Foto barang wajib diunggah
        if (fotoFile == null || fotoFile.isEmpty()) {
            redirectAttrs.addFlashAttribute("error", "Foto barang wajib diunggah!");
            return "redirect:/barang/tambah";
        }

        Barang barang = new Barang();
        barang.setPenjual(penjual);
        barang.setNamaBarang(namaBarang);
        barang.setHarga(harga);
        barang.setKondisiBarang(kondisiBarang);
        barang.setDeskripsi("[" + kategori + "] " + deskripsi);

        try {
            String contentType = fotoFile.getContentType() != null ? fotoFile.getContentType() : "image/jpeg";
            String base64Image = Base64.getEncoder().encodeToString(fotoFile.getBytes());
            barang.setFotoBarang("data:" + contentType + ";base64," + base64Image);
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Gagal memproses file foto. Coba gunakan gambar lain.");
            return "redirect:/barang/tambah";
        }

        if (aktifkanBoost != null && aktifkanBoost) {
            barang.setStatusListing("Menunggu Pembayaran");
            Barang savedBarang = barangRepo.save(barang);

            SuperBoost boost = new SuperBoost();
            boost.setBarang(savedBarang);
            boost.setDurasiJam(24);
            boost.setHargaBoost(new BigDecimal("5000.00"));
            boost.setWaktuMulai(LocalDateTime.now());
            boost.setWaktuSelesai(LocalDateTime.now().plusHours(24));
            boost.setStatusBoost("Pending");
            SuperBoost savedBoost = boostRepo.save(boost);

            return "redirect:/boost/pembayaran/" + savedBoost.getIdBoost();
        } else {
            barang.setStatusListing("Tersedia");
            barangRepo.save(barang);
            return "redirect:/";
        }
    }

    @GetMapping("/boost/pembayaran/{idBoost}")
    public String halamanPembayaranBoost(@PathVariable Integer idBoost, HttpSession session, Model model) {
        Pengguna userAktif = (Pengguna) session.getAttribute("userLogin");
        if (userAktif == null) return "redirect:/login";
        SuperBoost boost = boostRepo.findById(idBoost).orElseThrow();
        model.addAttribute("userAktif", userAktif);
        model.addAttribute("boost", boost);
        return "pembayaran-boost";
    }

    @PostMapping("/boost/bayar/{idBoost}")
    public String prosesBayarBoost(@PathVariable Integer idBoost) {
        SuperBoost boost = boostRepo.findById(idBoost).orElseThrow();
        boost.setStatusBoost("Aktif");
        boost.setWaktuMulai(LocalDateTime.now());
        boost.setWaktuSelesai(LocalDateTime.now().plusHours(24));
        boostRepo.save(boost);

        Barang barang = boost.getBarang();
        barang.setStatusListing("Tersedia");
        barangRepo.save(barang);

        return "redirect:/";
    }

    // ==========================================
    // BAGIAN TRANSAKSI PEMBELI & PENJUAL (QRIS)
    // ==========================================

    @GetMapping("/transaksi/ajukan/{idBarang}")
    public String formPengajuan(
            @PathVariable Integer idBarang,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            HttpSession session,
            Model model) {

        Pengguna userAktif = (Pengguna) session.getAttribute("userLogin");
        if (userAktif == null) return "redirect:/login";

        Barang barang = barangRepo.findById(idBarang).orElseThrow();

        // Cegah user membeli barangnya sendiri atau barang yang sudah Terjual
        if (barang.getPenjual().getIdPengguna().equals(userAktif.getIdPengguna()) ||
            !"Tersedia".equalsIgnoreCase(barang.getStatusListing())) {
            return "redirect:/";
        }

        List<CodPoint> semuaTitik = codPointRepo.findAll();

        String koordinatAktif = (lat != null && lon != null) 
                ? lat + ", " + lon 
                : userAktif.getKoordinatLokasi();

        CodPoint codPointTerpilih = cariCodPointTerdekat(koordinatAktif, semuaTitik);

        model.addAttribute("userAktif", userAktif);
        model.addAttribute("barang", barang);
        model.addAttribute("codPointTerpilih", codPointTerpilih);
        model.addAttribute("koordinatPembeli", koordinatAktif);
        return "form-pengajuan";
    }

    private CodPoint cariCodPointTerdekat(String koordUser, List<CodPoint> daftarTitik) {
        if (daftarTitik.isEmpty()) return new CodPoint();
        try {
            String[] partsUser = koordUser.split(",");
            double lat1 = Double.parseDouble(partsUser[0].trim());
            double lon1 = Double.parseDouble(partsUser[1].trim());

            CodPoint terdekat = daftarTitik.get(0);
            double jarakTerpendek = Double.MAX_VALUE;

            for (CodPoint titik : daftarTitik) {
                String[] partsTitik = titik.getKoordinatLokasi().split(",");
                double lat2 = Double.parseDouble(partsTitik[0].trim());
                double lon2 = Double.parseDouble(partsTitik[1].trim());

                double dLat = Math.toRadians(lat2 - lat1);
                double dLon = Math.toRadians(lon2 - lon1);
                double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                           Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                           Math.sin(dLon / 2) * Math.sin(dLon / 2);
                double jarak = 6371 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

                if (jarak < jarakTerpendek) {
                    jarakTerpendek = jarak;
                    terdekat = titik;
                }
            }
            return terdekat;
        } catch (Exception e) {
            return daftarTitik.get(0);
        }
    }

    @PostMapping("/transaksi/simpan")
    public String simpanTransaksi(
            @RequestParam Integer idBarang,
            @RequestParam Integer idCod,
            @RequestParam(defaultValue = "1") Integer jumlahBarang,
            @RequestParam String waktuPertemuan,
            HttpSession session) {

        Pengguna pembeli = (Pengguna) session.getAttribute("userLogin");
        if (pembeli == null) return "redirect:/login";

        Barang barang = barangRepo.findById(idBarang).orElseThrow();

        // Validasi backend: penjual tidak boleh membeli barang sendiri & barang harus masih Tersedia
        if (barang.getPenjual().getIdPengguna().equals(pembeli.getIdPengguna()) ||
            !"Tersedia".equalsIgnoreCase(barang.getStatusListing())) {
            return "redirect:/";
        }

        CodPoint codPoint = codPointRepo.findById(idCod).orElseThrow();
        BigDecimal biayaLayanan = new BigDecimal("2000.00");
        BigDecimal totalBayar = barang.getHarga().add(biayaLayanan);

        Transaksi trx = new Transaksi();
        trx.setBarang(barang);
        trx.setPembeli(pembeli);
        trx.setCodPoint(codPoint);
        trx.setKodePengajuan("TRX-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        trx.setJumlahBarang(1); // Dikunci 1 unit untuk barang preloved
        trx.setBiayaLayanan(biayaLayanan);
        trx.setTotalHarga(totalBayar);
        trx.setStatusTransaksi("Pengajuan");
        trx.setWaktuPertemuan(LocalDateTime.parse(waktuPertemuan));
        Transaksi savedTrx = transaksiRepo.save(trx);

        // Catat ke tabel pembayaran (Pending sampai di-ACC penjual & dibayar pembeli)
        Pembayaran bayar = new Pembayaran();
        bayar.setTransaksi(savedTrx);
        bayar.setMetodePembayaran("QRIS");
        bayar.setNominal(totalBayar);
        bayar.setStatusPembayaran("Pending");
        bayar.setStatusDana("Ditahan");
        pembayaranRepo.save(bayar);

        return "redirect:/transaksi";
    }

    @GetMapping("/transaksi")
    public String halamanRiwayatCod(HttpSession session, Model model) {
        Pengguna userAktif = (Pengguna) session.getAttribute("userLogin");
        if (userAktif == null) return "redirect:/login";

        List<Transaksi> transaksiPembeli = transaksiRepo.findAll().stream()
                .filter(t -> t.getPembeli().getIdPengguna().equals(userAktif.getIdPengguna()))
                .collect(Collectors.toList());

        Map<Integer, String> statusBayarMap = new HashMap<>();
        for (Transaksi t : transaksiPembeli) {
            pembayaranRepo.findByTransaksi(t).ifPresent(b ->
                statusBayarMap.put(t.getIdTransaksi(), b.getStatusPembayaran())
            );
        }

        Map<Integer, Ulasan> ulasanMap = new HashMap<>();
        for (Transaksi t : transaksiPembeli) {
            ulasanRepo.findByTransaksi(t).ifPresent(u -> ulasanMap.put(t.getIdTransaksi(), u));
        }

        model.addAttribute("userAktif", userAktif);
        model.addAttribute("daftarTransaksi", transaksiPembeli);
        model.addAttribute("statusBayarMap", statusBayarMap);
        model.addAttribute("ulasanMap", ulasanMap);
        return "riwayat-cod";
    }

    // Halaman Tampilkan QRIS setelah jadwal di-ACC penjual
    @GetMapping("/transaksi/bayar/{idTransaksi}")
    public String halamanBayarQris(@PathVariable Integer idTransaksi, HttpSession session, Model model) {
        Pengguna userAktif = (Pengguna) session.getAttribute("userLogin");
        if (userAktif == null) return "redirect:/login";

        Transaksi trx = transaksiRepo.findById(idTransaksi).orElseThrow();
        if (!trx.getPembeli().getIdPengguna().equals(userAktif.getIdPengguna())) {
            return "redirect:/transaksi";
        }

        model.addAttribute("userAktif", userAktif);
        model.addAttribute("trx", trx);
        return "pembayaran-cod";
    }

    // Proses Pembeli Konfirmasi Bayar QRIS -> Barang otomatis Terjual & hilang dari katalog
    @PostMapping("/transaksi/bayar/{idTransaksi}")
    public String prosesBayarQris(@PathVariable Integer idTransaksi, HttpSession session) {
        Pengguna userAktif = (Pengguna) session.getAttribute("userLogin");
        if (userAktif == null) return "redirect:/login";

        Transaksi trx = transaksiRepo.findById(idTransaksi).orElseThrow();
        if (trx.getPembeli().getIdPengguna().equals(userAktif.getIdPengguna())) {
            pembayaranRepo.findByTransaksi(trx).ifPresent(bayar -> {
                bayar.setStatusPembayaran("Berhasil");
                bayar.setStatusDana("Ditahan");
                bayar.setWaktuBayar(LocalDateTime.now());
                pembayaranRepo.save(bayar);
            });

            Barang barang = trx.getBarang();
            barang.setStatusListing("Terjual");
            barangRepo.save(barang);
        }
        return "redirect:/transaksi";
    }

    @GetMapping("/penjual")
    public String halamanPenjual(HttpSession session, Model model) {
        Pengguna userAktif = (Pengguna) session.getAttribute("userLogin");
        if (userAktif == null) return "redirect:/login";

        List<Transaksi> pesananMasuk = transaksiRepo.findAll().stream()
                .filter(t -> t.getBarang().getPenjual().getIdPengguna().equals(userAktif.getIdPengguna()))
                .collect(Collectors.toList());

        List<Barang> barangSaya = barangRepo.findAll().stream()
                .filter(b -> b.getPenjual().getIdPengguna().equals(userAktif.getIdPengguna()))
                .collect(Collectors.toList());

        List<Ulasan> ulasanToko = ulasanRepo.findByPenjual(userAktif);
        model.addAttribute("ulasanToko", ulasanToko);

        model.addAttribute("userAktif", userAktif);
        model.addAttribute("pesananMasuk", pesananMasuk);
        model.addAttribute("barangSaya", barangSaya);
        return "dashboard-penjual";
    }

    @PostMapping("/penjual/terima/{idTransaksi}")
    public String penjualTerimaCod(@PathVariable Integer idTransaksi, HttpSession session) {
        Pengguna userAktif = (Pengguna) session.getAttribute("userLogin");
        if (userAktif == null) return "redirect:/login";

        Transaksi trx = transaksiRepo.findById(idTransaksi).orElseThrow();
        if (trx.getBarang().getPenjual().getIdPengguna().equals(userAktif.getIdPengguna())) {
            trx.setStatusTransaksi("Menunggu Pertemuan");
            transaksiRepo.save(trx);
        }
        return "redirect:/penjual";
    }

    @PostMapping("/penjual/tolak/{idTransaksi}")
    public String penjualTolakCod(
            @PathVariable Integer idTransaksi,
            @RequestParam(defaultValue = "Jadwal atau titik temu tidak sesuai.") String alasanBatal,
            HttpSession session) {
        Pengguna userAktif = (Pengguna) session.getAttribute("userLogin");
        if (userAktif == null) return "redirect:/login";

        Transaksi trx = transaksiRepo.findById(idTransaksi).orElseThrow();
        if (trx.getBarang().getPenjual().getIdPengguna().equals(userAktif.getIdPengguna())) {
            trx.setStatusTransaksi("Batal");
            trx.setAlasanBatal(alasanBatal);
            transaksiRepo.save(trx);

            pembayaranRepo.findByTransaksi(trx).ifPresent(bayar -> {
                bayar.setStatusPembayaran("Gagal");
                bayar.setStatusDana("Dikembalikan");
                pembayaranRepo.save(bayar);
            });
        }
        return "redirect:/penjual";
    }

    @PostMapping("/penjual/konfirmasi-serah/{idTransaksi}")
    public String penjualKonfirmasiSerah(@PathVariable Integer idTransaksi, HttpSession session) {
        Pengguna userAktif = (Pengguna) session.getAttribute("userLogin");
        if (userAktif == null) return "redirect:/login";

        Transaksi trx = transaksiRepo.findById(idTransaksi).orElseThrow();
        if (trx.getBarang().getPenjual().getIdPengguna().equals(userAktif.getIdPengguna())) {
            trx.setWktKonfirmPenjual(LocalDateTime.now());
            selesaikanTransaksiJikaDuaArah(trx);
            transaksiRepo.save(trx);
        }
        return "redirect:/penjual";
    }

    @PostMapping("/transaksi/konfirmasi-terima/{idTransaksi}")
    public String pembeliKonfirmasiTerima(@PathVariable Integer idTransaksi, HttpSession session) {
        Pengguna userAktif = (Pengguna) session.getAttribute("userLogin");
        if (userAktif == null) return "redirect:/login";

        Transaksi trx = transaksiRepo.findById(idTransaksi).orElseThrow();
        if (trx.getPembeli().getIdPengguna().equals(userAktif.getIdPengguna())) {
            trx.setWktKonfirmPembeli(LocalDateTime.now());
            selesaikanTransaksiJikaDuaArah(trx);
            transaksiRepo.save(trx);
        }
        return "redirect:/transaksi";
    }

    // Helper: Jika penjual & pembeli sudah konfirmasi, selesaikan transaksi & teruskan dana QRIS
    private void selesaikanTransaksiJikaDuaArah(Transaksi trx) {
        if (trx.getWktKonfirmPenjual() != null && trx.getWktKonfirmPembeli() != null) {
            trx.setStatusTransaksi("Selesai");

            Barang barang = trx.getBarang();
            barang.setStatusListing("Terjual");
            barangRepo.save(barang);

            pembayaranRepo.findByTransaksi(trx).ifPresent(bayar -> {
                bayar.setStatusPembayaran("Berhasil");
                bayar.setStatusDana("Diteruskan");
                if (bayar.getWaktuBayar() == null) {
                    bayar.setWaktuBayar(LocalDateTime.now());
                }
                pembayaranRepo.save(bayar);
            });
        }
    }

    @PostMapping("/transaksi/ulasan/{idTransaksi}")
    public String simpanUlasan(
            @PathVariable Integer idTransaksi,
            @RequestParam Integer rating,
            @RequestParam(required = false) String komentar,
            HttpSession session) {

        Pengguna userAktif = (Pengguna) session.getAttribute("userLogin");
        if (userAktif == null) return "redirect:/login";

        Transaksi trx = transaksiRepo.findById(idTransaksi).orElseThrow();
        if ("Selesai".equalsIgnoreCase(trx.getStatusTransaksi()) &&
            trx.getPembeli().getIdPengguna().equals(userAktif.getIdPengguna()) &&
            ulasanRepo.findByTransaksi(trx).isEmpty()) {

            Ulasan ulasan = new Ulasan();
            ulasan.setTransaksi(trx);
            ulasan.setPengulas(userAktif);
            ulasan.setPenjual(trx.getBarang().getPenjual());
            ulasan.setRating(Math.max(1, Math.min(5, rating)));
            ulasan.setKomentar(komentar != null && !komentar.trim().isEmpty() ? komentar.trim() : "Transaksi COD aman & lancar.");
            ulasan.setWaktuUlasan(LocalDateTime.now());
            ulasanRepo.save(ulasan);
        }
        return "redirect:/transaksi";
    }
}