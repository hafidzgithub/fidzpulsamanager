import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.jetbrains.annotations.Contract;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    /* access modifiers changed from: private */
    public ArrayAdapter<String> adapter;
    private DrawerLayout dlMain;
    private ActionBarDrawerToggle dlToggle;
    int llrHasil;
    int llrLabaKotor;
    int llrPengeluaranLaba;
    int llrSaldoKeluar;
    int llrUangMasuk;
    /* access modifiers changed from: private */
    public ArrayList<String> penghutang;
    TextView tvLabaNominal;
    TextView tvLog;
    TextView tvSaldoNominal;
    TextView tvUangNominal;
    TextView tvUtangNominal;

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", 0).edit();
        editor.putString("uang", this.tvUangNominal.getText().toString());
        editor.putString("utang", this.tvUtangNominal.getText().toString());
        editor.putString("saldo", this.tvSaldoNominal.getText().toString());
        editor.putString("laba", this.tvLabaNominal.getText().toString());
        editor.putInt("llrUangMasuk", this.llrUangMasuk);
        editor.putInt("llrSaldoKeluar", this.llrSaldoKeluar);
        editor.putInt("llrPengeluaranLaba", this.llrPengeluaranLaba);
        editor.commit();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", 0).edit();
        editor.putString("uang", this.tvUangNominal.getText().toString());
        editor.putString("utang", this.tvUtangNominal.getText().toString());
        editor.putString("saldo", this.tvSaldoNominal.getText().toString());
        editor.putString("laba", this.tvLabaNominal.getText().toString());
        editor.putInt("llrUangMasuk", this.llrUangMasuk);
        editor.putInt("llrSaldoKeluar", this.llrSaldoKeluar);
        editor.putInt("llrPengeluaranLaba", this.llrPengeluaranLaba);
        editor.commit();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_main);
        Boolean isFirstRun = Boolean.valueOf(getSharedPreferences("PREFERENCE", 0).getBoolean("isfirstrun", true));
        SharedPreferences settings = getSharedPreferences("MyPrefs", 0);
        this.llrUangMasuk = settings.getInt("llrUangMasuk", 0);
        this.llrSaldoKeluar = settings.getInt("llrSaldoKeluar", 0);
        this.llrPengeluaranLaba = settings.getInt("llrPengeluaranLaba", 0);
        this.tvUangNominal = (TextView) findViewById(R.id.tvUangNominal);
        this.tvUtangNominal = (TextView) findViewById(R.id.tvUtangNominal);
        this.tvSaldoNominal = (TextView) findViewById(R.id.tvSaldoNominal);
        this.tvLabaNominal = (TextView) findViewById(R.id.tvLabaNominal);
        this.tvLog = (TextView) findViewById(R.id.tvLog);
        Button bTransaksiPenjualan = (Button) findViewById(R.id.bTransaksiPenjualan);
        Button bPembayaranUtang = (Button) findViewById(R.id.bPembayaranUtang);
        Button bPembelianSaldo = (Button) findViewById(R.id.bPembelianSaldo);
        Button bLainLain = (Button) findViewById(R.id.bLainLain);
        Button bTambahUang = (Button) findViewById(R.id.bTambahUang);
        Button bLogTransaksi = (Button) findViewById(R.id.bLogTransaksi);
        NavigationView nvNavigation = (NavigationView) findViewById(R.id.dlnvNavigation);
        nvNavigation.bringToFront();
        nvNavigation.setNavigationItemSelectedListener(this);
        this.dlMain = (DrawerLayout) findViewById(R.id.dlMain);
        this.dlToggle = new ActionBarDrawerToggle(this, this.dlMain, R.string.open, R.string.close);
        this.dlMain.addDrawerListener(this.dlToggle);
        this.dlToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (isFirstRun.booleanValue()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View kondisiAwalView = getLayoutInflater().inflate(R.layout.layout_kondisi_awal, (ViewGroup) null);
            builder.setView(kondisiAwalView);
            final AlertDialog dialog = builder.create();
            dialog.show();
            final EditText etKondisiAwalUang = (EditText) kondisiAwalView.findViewById(R.id.etKondisiAwalUang);
            etKondisiAwalUang.addTextChangedListener(onTextChangedListener(etKondisiAwalUang));
            final EditText etKondisiAwalSaldo = (EditText) kondisiAwalView.findViewById(R.id.etKondisiAwalSaldo);
            etKondisiAwalSaldo.addTextChangedListener(onTextChangedListener(etKondisiAwalSaldo));
            final EditText etKondisiAwalLaba = (EditText) kondisiAwalView.findViewById(R.id.etKondisiAwalLaba);
            etKondisiAwalLaba.addTextChangedListener(onTextChangedListener(etKondisiAwalLaba));
            ((Button) kondisiAwalView.findViewById(R.id.bKonfirmasi)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (etKondisiAwalUang.getText().toString().matches("")) {
                        etKondisiAwalUang.setText("0");
                    }
                    if (etKondisiAwalSaldo.getText().toString().matches("")) {
                        etKondisiAwalSaldo.setText("0");
                    }
                    if (etKondisiAwalLaba.getText().toString().matches("")) {
                        etKondisiAwalLaba.setText("0");
                    }
                    MainActivity.this.tvUangNominal.setText(etKondisiAwalUang.getText().toString());
                    MainActivity.this.tvSaldoNominal.setText(etKondisiAwalSaldo.getText().toString());
                    MainActivity.this.tvLabaNominal.setText(etKondisiAwalLaba.getText().toString());
                    dialog.dismiss();
                }
            });
            getSharedPreferences("PREFERENCE", 0).edit().putBoolean("isfirstrun", false).commit();
        }
        bLogTransaksi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder log = new AlertDialog.Builder(MainActivity.this);
                View logView = MainActivity.this.getLayoutInflater().inflate(R.layout.layout_log, (ViewGroup) null);
                log.setView(logView);
                final AlertDialog dialogLog = log.create();
                dialogLog.show();
                final TextView tvLog = (TextView) logView.findViewById(R.id.tvLog);
                tvLog.setText(MainActivity.this.readFromFile(MainActivity.this));
                tvLog.setMovementMethod(new ScrollingMovementMethod());
                ((Button) logView.findViewById(R.id.bEditLog)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        AlertDialog.Builder editLog = new AlertDialog.Builder(MainActivity.this);
                        View logView = MainActivity.this.getLayoutInflater().inflate(R.layout.layout_edit_log, (ViewGroup) null);
                        editLog.setView(logView);
                        final AlertDialog dialogEditLog = editLog.create();
                        dialogEditLog.show();
                        final EditText etLog = (EditText) logView.findViewById(R.id.etLog);
                        etLog.setText(MainActivity.this.readFromFile(MainActivity.this));
                        etLog.setMovementMethod(new ScrollingMovementMethod());
                        ((Button) logView.findViewById(R.id.bEditLogSimpan)).setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                MainActivity.this.writeToFile(etLog.getText().toString(), MainActivity.this);
                                dialogLog.dismiss();
                                dialogEditLog.dismiss();
                                Toast.makeText(MainActivity.this, "Log Tersimpan", 0).show();
                            }
                        });
                    }
                });
                ((Button) logView.findViewById(R.id.bSimpanLog)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        if (Build.VERSION.SDK_INT >= 23 && MainActivity.this.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                            MainActivity.this.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1000);
                        }
                        MainActivity.this.saveTextAsFile(tvLog.getText().toString());
                    }
                });
                ((Button) logView.findViewById(R.id.bHapusLog)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle((CharSequence) "Konfirmasi");
                        builder.setMessage((CharSequence) "Apakah anda yakin ingin menghapus log?");
                        builder.setPositiveButton((CharSequence) "Iya", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.this.hapusLog(MainActivity.this);
                                dialogLog.dismiss();
                            }
                        });
                        builder.setNegativeButton((CharSequence) "Tidak", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    }
                });
            }
        });
        bTambahUang.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder transaksiPenjualan = new AlertDialog.Builder(MainActivity.this);
                View transaksiPenjualanView = MainActivity.this.getLayoutInflater().inflate(R.layout.layout_tambah_uang, (ViewGroup) null);
                transaksiPenjualan.setView(transaksiPenjualanView);
                final AlertDialog dialog = transaksiPenjualan.create();
                dialog.show();
                ((Button) transaksiPenjualanView.findViewById(R.id.bBatal)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                final EditText etTambahUang = (EditText) transaksiPenjualanView.findViewById(R.id.etTambahUang);
                etTambahUang.addTextChangedListener(MainActivity.this.onTextChangedListener(etTambahUang));
                final TextView tvKetentuan = (TextView) transaksiPenjualanView.findViewById(R.id.tvKetentuan);
                ((Button) transaksiPenjualanView.findViewById(R.id.bKonfirmasi)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        if (etTambahUang.getText().toString().matches("")) {
                            tvKetentuan.setVisibility(0);
                            return;
                        }
                        MainActivity.this.tvUangNominal.setText(MainActivity.currencyFormat(Integer.toString(Integer.parseInt(MainActivity.this.tvUangNominal.getText().toString().replace(",", "")) + Integer.parseInt(etTambahUang.getText().toString().replace(",", "")))));
                        MainActivity.this.writeToFileTambahUang(etTambahUang.getText().toString(), MainActivity.this);
                        dialog.dismiss();
                    }
                });
            }
        });
        bTransaksiPenjualan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View transaksiPenjualanView = MainActivity.this.getLayoutInflater().inflate(R.layout.layout_transaksi_penjualan, (ViewGroup) null);
                builder.setView(transaksiPenjualanView);
                final AlertDialog dialog = builder.create();
                dialog.show();
                ((Button) transaksiPenjualanView.findViewById(R.id.bBatal)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                final EditText etTambahUang = (EditText) transaksiPenjualanView.findViewById(R.id.etTambahUang);
                etTambahUang.addTextChangedListener(MainActivity.this.onTextChangedListener(etTambahUang));
                final EditText etTambahUtang = (EditText) transaksiPenjualanView.findViewById(R.id.etTambahUtang);
                etTambahUtang.addTextChangedListener(MainActivity.this.onTextChangedListener(etTambahUtang));
                final EditText etKurangiSaldo = (EditText) transaksiPenjualanView.findViewById(R.id.etKurangiSaldo);
                etKurangiSaldo.addTextChangedListener(MainActivity.this.onTextChangedListener(etKurangiSaldo));
                final AutoCompleteTextView etNamaPenghutang = (AutoCompleteTextView) transaksiPenjualanView.findViewById(R.id.etNamaPenghutang);
                final EditText etKeteranganTP = (EditText) transaksiPenjualanView.findViewById(R.id.etKeteranganTP);
                etNamaPenghutang.setAdapter(new ArrayAdapter<>(MainActivity.this, 17367043, MainActivity.this.readNamaFromFile(MainActivity.this).split("\\r?\\n")));
                final EditText etNomorHP = (EditText) transaksiPenjualanView.findViewById(R.id.etNomorHp);
                final TextView tvKetentuan = (TextView) transaksiPenjualanView.findViewById(R.id.tvKetentuan);
                final TextView tvKetentuanNama = (TextView) transaksiPenjualanView.findViewById(R.id.tvKetentuanNama);
                ((Button) transaksiPenjualanView.findViewById(R.id.bKonfirmasi)).setOnClickListener(new View.OnClickListener() {
                    @TargetApi(26)
                    @RequiresApi(api = 19)
                    public void onClick(View view) {
                        String namanyaPenghutangFix;
                        int hutangFix;
                        if (etKurangiSaldo.getText().toString().matches("")) {
                            tvKetentuan.setVisibility(0);
                            return;
                        }
                        int spaceCount = 0;
                        for (char c : etNamaPenghutang.getText().toString().toCharArray()) {
                            if (c == ' ') {
                                spaceCount++;
                            }
                        }
                        if (spaceCount > 2) {
                            tvKetentuanNama.setVisibility(0);
                            return;
                        }
                        if (etTambahUang.getText().toString().matches("")) {
                            etTambahUang.setText("0");
                        } else if (etTambahUtang.getText().toString().matches("")) {
                            etTambahUtang.setText("0");
                        }
                        MainActivity.this.writeToFileTambahNamaPenghutang(etNamaPenghutang.getText().toString(), MainActivity.this);
                        int nilaiUang = Integer.parseInt(MainActivity.this.tvUangNominal.getText().toString().replace(",", ""));
                        String fSEtTambahUang = etTambahUang.getText().toString().replace(",", "");
                        MainActivity.this.tvUangNominal.setText(MainActivity.currencyFormat(Integer.toString(nilaiUang + Integer.parseInt(fSEtTambahUang))));
                        MainActivity.this.llrUangMasuk += Integer.parseInt(fSEtTambahUang);
                        int nilaiUtang = Integer.parseInt(MainActivity.this.tvUtangNominal.getText().toString().replace(",", ""));
                        String fSEtTambahUtang = etTambahUtang.getText().toString().replace(",", "");
                        MainActivity.this.tvUtangNominal.setText(MainActivity.currencyFormat(Integer.toString(nilaiUtang + Integer.parseInt(fSEtTambahUtang))));
                        String namaPenghutang = etNamaPenghutang.getText().toString();
                        if (namaPenghutang.equals("")) {
                            namaPenghutang = "Pembeli";
                        }
                        MainActivity.this.writeToFileTambahHutang(etTambahUtang.getText().toString(), namaPenghutang, etKurangiSaldo.getText().toString(), etNomorHP.getText().toString(), etKeteranganTP.getText().toString(), MainActivity.this);
                        MainActivity.this.writeToFileTambahBeli(etTambahUang.getText().toString(), namaPenghutang, etKurangiSaldo.getText().toString(), etNomorHP.getText().toString(), etKeteranganTP.getText().toString(), MainActivity.this);
                        MainActivity.this.llrUangMasuk += Integer.parseInt(fSEtTambahUtang);
                        int nilaiSaldo = Integer.parseInt(MainActivity.this.tvSaldoNominal.getText().toString().replace(",", ""));
                        String fSEtKurangiSaldo = etKurangiSaldo.getText().toString().replace(",", "");
                        MainActivity.this.tvSaldoNominal.setText(MainActivity.currencyFormat(Integer.toString(nilaiSaldo - Integer.parseInt(fSEtKurangiSaldo))));
                        MainActivity.this.llrSaldoKeluar += Integer.parseInt(fSEtKurangiSaldo);
                        MainActivity.this.tvLabaNominal.setText(MainActivity.currencyFormat(Integer.toString(((Integer.parseInt(MainActivity.this.tvLabaNominal.getText().toString().replace(",", "")) + Integer.parseInt(fSEtTambahUang)) + Integer.parseInt(fSEtTambahUtang)) - Integer.parseInt(fSEtKurangiSaldo))));
                        if (!etTambahUtang.getText().toString().equals("0")) {
                            ArrayList unused = MainActivity.this.penghutang = FileHelper.readData(MainActivity.this);
                            for (int i = 0; i < MainActivity.this.penghutang.size(); i++) {
                                String[] namanyaPenghutangArray = ((String) MainActivity.this.penghutang.get(i)).toString().split("\\s+");
                                if (namanyaPenghutangArray.length == 8) {
                                    namanyaPenghutangFix = namanyaPenghutangArray[3] + namanyaPenghutangArray[4] + namanyaPenghutangArray[5];
                                } else if (namanyaPenghutangArray.length == 7) {
                                    namanyaPenghutangFix = namanyaPenghutangArray[3] + namanyaPenghutangArray[4];
                                } else {
                                    namanyaPenghutangFix = namanyaPenghutangArray[3];
                                }
                                if (namaPenghutang.equals(namanyaPenghutangFix)) {
                                    String[] hutangnyaPenghutangArray = ((String) MainActivity.this.penghutang.get(i)).toString().split("\\s+");
                                    if (hutangnyaPenghutangArray.length == 8) {
                                        int hasilHutang = Integer.parseInt(hutangnyaPenghutangArray[7].replace(",", "")) + Integer.parseInt(fSEtTambahUtang);
                                        hutangnyaPenghutangArray[7] = Integer.toString(hasilHutang);
                                        hutangFix = hasilHutang;
                                    } else if (hutangnyaPenghutangArray.length == 7) {
                                        int hasilHutang2 = Integer.parseInt(hutangnyaPenghutangArray[6].replace(",", "")) + Integer.parseInt(fSEtTambahUtang);
                                        hutangnyaPenghutangArray[6] = Integer.toString(hasilHutang2);
                                        hutangFix = hasilHutang2;
                                    } else {
                                        int hasilHutang3 = Integer.parseInt(hutangnyaPenghutangArray[5].replace(",", "")) + Integer.parseInt(fSEtTambahUtang);
                                        hutangnyaPenghutangArray[5] = Integer.toString(hasilHutang3);
                                        hutangFix = hasilHutang3;
                                    }
                                    MainActivity.this.penghutang.remove(i);
                                    ArrayAdapter unused2 = MainActivity.this.adapter = new ArrayAdapter(MainActivity.this, 17367043, MainActivity.this.penghutang);
                                    MainActivity.this.adapter.notifyDataSetChanged();
                                    MainActivity.this.adapter.add(new SimpleDateFormat("dd/MM/yyyy : HH:mm").format(new Date()) + System.lineSeparator() + namaPenghutang + " hutang " + MainActivity.currencyFormat(Integer.toString(hutangFix)));
                                    FileHelper.WriteData(MainActivity.this.penghutang, MainActivity.this);
                                    dialog.dismiss();
                                    return;
                                }
                            }
                            ArrayAdapter unused3 = MainActivity.this.adapter = new ArrayAdapter(MainActivity.this, 17367043, MainActivity.this.penghutang);
                            MainActivity.this.adapter.add(new SimpleDateFormat("dd/MM/yyyy : HH:mm").format(new Date()) + System.lineSeparator() + namaPenghutang + " hutang " + etTambahUtang.getText().toString());
                            FileHelper.WriteData(MainActivity.this.penghutang, MainActivity.this);
                        }
                        dialog.dismiss();
                    }
                });
            }
        });
        final TextView[] textViewArr = new TextView[1];
        bPembayaranUtang.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final AlertDialog.Builder pembayaranUtang = new AlertDialog.Builder(MainActivity.this);
                View pembayaranUtangView = MainActivity.this.getLayoutInflater().inflate(R.layout.layout_pembayaran_utang, (ViewGroup) null);
                pembayaranUtang.setView(pembayaranUtangView);
                final AlertDialog dialogPU = pembayaranUtang.create();
                dialogPU.show();
                ListView penghutangList = (ListView) pembayaranUtangView.findViewById(R.id.listPenghutang);
                textViewArr[0] = (TextView) pembayaranUtangView.findViewById(R.id.statusHutang);
                ArrayList unused = MainActivity.this.penghutang = FileHelper.readData(pembayaranUtang.getContext());
                ArrayAdapter unused2 = MainActivity.this.adapter = new ArrayAdapter(pembayaranUtang.getContext(), 17367043, MainActivity.this.penghutang);
                penghutangList.setAdapter(MainActivity.this.adapter);
                penghutangList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                        final String data = (String) adapterView.getItemAtPosition(i);
                        AlertDialog.Builder builder = new AlertDialog.Builder(pembayaranUtang.getContext());
                        builder.setTitle((CharSequence) "Konfirmasi");
                        builder.setMessage((CharSequence) "Pilih metode pembayaran hutang");
                        builder.setPositiveButton((CharSequence) "Lunas", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                int currentHutangInt;
                                String[] number = data.split("\\s+");
                                int hutang = Integer.parseInt(MainActivity.this.tvUtangNominal.getText().toString().replace(",", ""));
                                if (number.length == 8) {
                                    currentHutangInt = Integer.parseInt(number[7].replace(",", ""));
                                } else if (number.length == 7) {
                                    currentHutangInt = Integer.parseInt(number[6].replace(",", ""));
                                } else {
                                    currentHutangInt = Integer.parseInt(number[5].replace(",", ""));
                                }
                                MainActivity.this.tvUtangNominal.setText(MainActivity.currencyFormat(String.valueOf(hutang - currentHutangInt)));
                                MainActivity.this.tvUangNominal.setText(MainActivity.currencyFormat(String.valueOf(Integer.parseInt(MainActivity.this.tvUangNominal.getText().toString().replace(",", "")) + currentHutangInt)));
                                MainActivity.this.penghutang.remove(i);
                                MainActivity.this.adapter.notifyDataSetChanged();
                                FileHelper.WriteData(MainActivity.this.penghutang, pembayaranUtang.getContext());
                                if (number.length == 8) {
                                    MainActivity.this.writeToFilePembayaranUtang(number[3] + " " + number[4] + " " + number[5], pembayaranUtang.getContext());
                                } else if (number.length == 7) {
                                    MainActivity.this.writeToFilePembayaranUtang(number[3] + " " + number[4], pembayaranUtang.getContext());
                                } else {
                                    MainActivity.this.writeToFilePembayaranUtang(number[3], pembayaranUtang.getContext());
                                }
                                dialog.dismiss();
                                if (MainActivity.this.adapter.getCount() == 0) {
                                    textViewArr[0].setVisibility(0);
                                    dialogPU.dismiss();
                                }
                            }
                        });
                        builder.setNegativeButton((CharSequence) "Cicil", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                int currentHutangInt;
                                AlertDialog.Builder cicilHutang = new AlertDialog.Builder(MainActivity.this);
                                View cicilHutangView = MainActivity.this.getLayoutInflater().inflate(R.layout.layout_cicil_hutang, (ViewGroup) null);
                                cicilHutang.setView(cicilHutangView);
                                final AlertDialog dialogCicilHutang = cicilHutang.create();
                                dialogCicilHutang.show();
                                final EditText etCicil = (EditText) cicilHutangView.findViewById(R.id.lchetCicil);
                                etCicil.addTextChangedListener(MainActivity.this.onTextChangedListener(etCicil));
                                Button bKonfirmasi = (Button) cicilHutangView.findViewById(R.id.lchbKonfirmasi);
                                Button bKembali = (Button) cicilHutangView.findViewById(R.id.lchbKembali);
                                etCicil.addTextChangedListener(MainActivity.this.onTextChangedListener(etCicil));
                                final String[] number = data.split("\\s+");
                                final TextView tvNominalHutanglch = (TextView) cicilHutangView.findViewById(R.id.lchtvHutangNominal);
                                if (number.length == 8) {
                                    currentHutangInt = Integer.parseInt(number[7].replace(",", ""));
                                } else if (number.length == 7) {
                                    currentHutangInt = Integer.parseInt(number[6].replace(",", ""));
                                } else {
                                    currentHutangInt = Integer.parseInt(number[5].replace(",", ""));
                                }
                                tvNominalHutanglch.setText(MainActivity.currencyFormat(Integer.toString(currentHutangInt)));
                                bKonfirmasi.setOnClickListener(new View.OnClickListener() {
                                    @RequiresApi(api = 19)
                                    public void onClick(View view) {
                                        String namaPenghutang;
                                        if (etCicil.getText().toString().equals("")) {
                                            Toast.makeText(MainActivity.this, "Masukkan Nominal Cicil", 0).show();
                                            return;
                                        }
                                        String stringCicil = etCicil.getText().toString();
                                        int cicil = Integer.parseInt(stringCicil.replace(",", ""));
                                        int sisaHutang = Integer.parseInt(tvNominalHutanglch.getText().toString().replace(",", "")) - cicil;
                                        if (number.length == 8) {
                                            namaPenghutang = number[3] + " " + number[4] + " " + number[5];
                                        } else if (number.length == 7) {
                                            namaPenghutang = number[3] + " " + number[4];
                                        } else {
                                            namaPenghutang = number[3];
                                        }
                                        if (sisaHutang < 0) {
                                            Toast.makeText(MainActivity.this, "Nominal cicil tidak bisa lebih besar dari hutang", 0).show();
                                            return;
                                        }
                                        if (sisaHutang == 0) {
                                            MainActivity.this.tvUtangNominal.setText(MainActivity.currencyFormat(String.valueOf(Integer.parseInt(MainActivity.this.tvUtangNominal.getText().toString().replace(",", "")) - cicil)));
                                            MainActivity.this.tvUangNominal.setText(MainActivity.currencyFormat(String.valueOf(Integer.parseInt(MainActivity.this.tvUangNominal.getText().toString().replace(",", "")) + cicil)));
                                            MainActivity.this.penghutang.remove(i);
                                            MainActivity.this.adapter.notifyDataSetChanged();
                                            FileHelper.WriteData(MainActivity.this.penghutang, pembayaranUtang.getContext());
                                            if (number.length == 8) {
                                                MainActivity.this.writeToFilePembayaranUtang(number[3] + " " + number[4] + " " + number[5], pembayaranUtang.getContext());
                                            } else if (number.length == 7) {
                                                MainActivity.this.writeToFilePembayaranUtang(number[3] + " " + number[4], pembayaranUtang.getContext());
                                            } else {
                                                MainActivity.this.writeToFilePembayaranUtang(number[3], pembayaranUtang.getContext());
                                            }
                                        } else {
                                            MainActivity.this.tvUtangNominal.setText(MainActivity.currencyFormat(String.valueOf(Integer.parseInt(MainActivity.this.tvUtangNominal.getText().toString().replace(",", "")) - cicil)));
                                            MainActivity.this.tvUangNominal.setText(MainActivity.currencyFormat(String.valueOf(Integer.parseInt(MainActivity.this.tvUangNominal.getText().toString().replace(",", "")) + cicil)));
                                            MainActivity.this.penghutang.remove(i);
                                            MainActivity.this.adapter.notifyDataSetChanged();
                                            FileHelper.WriteData(MainActivity.this.penghutang, pembayaranUtang.getContext());
                                            ArrayList unused = MainActivity.this.penghutang = FileHelper.readData(MainActivity.this);
                                            ArrayAdapter unused2 = MainActivity.this.adapter = new ArrayAdapter(MainActivity.this, 17367043, MainActivity.this.penghutang);
                                            MainActivity.this.adapter.add(new SimpleDateFormat("dd/MM/yyyy : HH:mm").format(new Date()) + System.lineSeparator() + namaPenghutang + " hutang " + MainActivity.currencyFormat(Integer.toString(sisaHutang)));
                                            FileHelper.WriteData(MainActivity.this.penghutang, MainActivity.this);
                                            if (number.length == 8) {
                                                MainActivity.this.writeToFilePembayaranCicil(number[3] + " " + number[4] + " " + number[5], stringCicil, pembayaranUtang.getContext());
                                            } else if (number.length == 7) {
                                                MainActivity.this.writeToFilePembayaranCicil(number[3] + " " + number[4], stringCicil, pembayaranUtang.getContext());
                                            } else {
                                                MainActivity.this.writeToFilePembayaranCicil(number[3], stringCicil, pembayaranUtang.getContext());
                                            }
                                        }
                                        dialogCicilHutang.dismiss();
                                        dialogPU.dismiss();
                                        if (MainActivity.this.adapter.getCount() == 0) {
                                            textViewArr[0].setVisibility(0);
                                            dialogPU.dismiss();
                                        }
                                    }
                                });
                                bKembali.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View view) {
                                        dialogCicilHutang.dismiss();
                                    }
                                });
                            }
                        });
                        builder.create().show();
                    }
                });
                if (MainActivity.this.adapter.getCount() == 0) {
                    textViewArr[0].setVisibility(0);
                }
            }
        });
        bPembelianSaldo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder pembelianSaldo = new AlertDialog.Builder(MainActivity.this);
                View pembelianSaldoView = MainActivity.this.getLayoutInflater().inflate(R.layout.layout_pembelian_saldo, (ViewGroup) null);
                pembelianSaldo.setView(pembelianSaldoView);
                final AlertDialog dialog = pembelianSaldo.create();
                dialog.show();
                ((Button) pembelianSaldoView.findViewById(R.id.bBatal)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                final EditText etTambahSaldo = (EditText) pembelianSaldoView.findViewById(R.id.etTambahSaldo);
                etTambahSaldo.addTextChangedListener(MainActivity.this.onTextChangedListener(etTambahSaldo));
                final TextView tvKetentuan = (TextView) pembelianSaldoView.findViewById(R.id.tvKetentuan);
                ((Button) pembelianSaldoView.findViewById(R.id.bKonfirmasi)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        if (etTambahSaldo.getText().toString().matches("")) {
                            tvKetentuan.setVisibility(0);
                            return;
                        }
                        int nilaiSaldo = Integer.parseInt(MainActivity.this.tvSaldoNominal.getText().toString().replace(",", ""));
                        String fSEtTambahSaldo = etTambahSaldo.getText().toString().replace(",", "");
                        MainActivity.this.tvSaldoNominal.setText(MainActivity.currencyFormat(Integer.toString(nilaiSaldo + Integer.parseInt(fSEtTambahSaldo))));
                        MainActivity.this.tvUangNominal.setText(MainActivity.currencyFormat(Integer.toString(Integer.parseInt(MainActivity.this.tvUangNominal.getText().toString().replace(",", "")) - Integer.parseInt(fSEtTambahSaldo))));
                        MainActivity.this.writeToFilePembelianSaldo(etTambahSaldo.getText().toString(), MainActivity.this);
                        dialog.dismiss();
                    }
                });
            }
        });
        bLainLain.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder lainLain = new AlertDialog.Builder(MainActivity.this);
                View lainLainView = MainActivity.this.getLayoutInflater().inflate(R.layout.layout_pengambilan_laba, (ViewGroup) null);
                lainLain.setView(lainLainView);
                final AlertDialog dialog = lainLain.create();
                dialog.show();
                ((Button) lainLainView.findViewById(R.id.bBatal)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                final EditText etKurangiLaba = (EditText) lainLainView.findViewById(R.id.etKurangiLaba);
                etKurangiLaba.addTextChangedListener(MainActivity.this.onTextChangedListener(etKurangiLaba));
                final EditText etKeteranganLaba = (EditText) lainLainView.findViewById(R.id.etKeteranganLaba);
                final TextView tvKetentuan = (TextView) lainLainView.findViewById(R.id.tvKetentuan);
                ((Button) lainLainView.findViewById(R.id.bKonfirmasi)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        if (etKurangiLaba.getText().toString().matches("")) {
                            tvKetentuan.setVisibility(0);
                            return;
                        }
                        int nilaiLaba = Integer.parseInt(MainActivity.this.tvLabaNominal.getText().toString().replace(",", ""));
                        String fSEtKurangiLaba = etKurangiLaba.getText().toString().replace(",", "");
                        MainActivity.this.tvLabaNominal.setText(MainActivity.currencyFormat(Integer.toString(nilaiLaba - Integer.parseInt(fSEtKurangiLaba))));
                        MainActivity.this.llrPengeluaranLaba += Integer.parseInt(fSEtKurangiLaba);
                        MainActivity.this.tvUangNominal.setText(MainActivity.currencyFormat(Integer.toString(Integer.parseInt(MainActivity.this.tvUangNominal.getText().toString().replace(",", "")) - Integer.parseInt(fSEtKurangiLaba))));
                        MainActivity.this.writeToFilePengambilanLaba(etKurangiLaba.getText().toString(), etKeteranganLaba.getText().toString(), MainActivity.this);
                        dialog.dismiss();
                    }
                });
            }
        });
        this.tvUangNominal.setText(settings.getString("uang", "0"));
        this.tvUtangNominal.setText(settings.getString("utang", "0"));
        this.tvSaldoNominal.setText(settings.getString("saldo", "0"));
        this.tvLabaNominal.setText(settings.getString("laba", "0"));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (this.dlToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.mCaraMenggunakan:
                startActivity(new Intent(this, Player.class));
                break;
            case R.id.mMasukkanKondisiAwal:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View kondisiAwalView = getLayoutInflater().inflate(R.layout.layout_kondisi_awal, (ViewGroup) null);
                builder.setView(kondisiAwalView);
                final AlertDialog dialogKondisiAwal = builder.create();
                dialogKondisiAwal.show();
                final EditText etKondisiAwalUang = (EditText) kondisiAwalView.findViewById(R.id.etKondisiAwalUang);
                etKondisiAwalUang.addTextChangedListener(onTextChangedListener(etKondisiAwalUang));
                final EditText etKondisiAwalSaldo = (EditText) kondisiAwalView.findViewById(R.id.etKondisiAwalSaldo);
                etKondisiAwalSaldo.addTextChangedListener(onTextChangedListener(etKondisiAwalSaldo));
                final EditText etKondisiAwalLaba = (EditText) kondisiAwalView.findViewById(R.id.etKondisiAwalLaba);
                etKondisiAwalLaba.addTextChangedListener(onTextChangedListener(etKondisiAwalLaba));
                ((Button) kondisiAwalView.findViewById(R.id.bKonfirmasi)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        if (etKondisiAwalUang.getText().toString().matches("")) {
                            etKondisiAwalUang.setText("0");
                        }
                        if (etKondisiAwalSaldo.getText().toString().matches("")) {
                            etKondisiAwalSaldo.setText("0");
                        }
                        if (etKondisiAwalLaba.getText().toString().matches("")) {
                            etKondisiAwalLaba.setText("0");
                        }
                        MainActivity.this.tvUangNominal.setText(etKondisiAwalUang.getText().toString());
                        MainActivity.this.tvSaldoNominal.setText(etKondisiAwalSaldo.getText().toString());
                        MainActivity.this.tvLabaNominal.setText(etKondisiAwalLaba.getText().toString());
                        dialogKondisiAwal.dismiss();
                    }
                });
                break;
            case R.id.mEditKondisi:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                View editKondisiView = getLayoutInflater().inflate(R.layout.layout_edit_kondisi, (ViewGroup) null);
                builder2.setView(editKondisiView);
                final AlertDialog dialogEditKondisi = builder2.create();
                dialogEditKondisi.show();
                final EditText etKondisiAwalUangEK = (EditText) editKondisiView.findViewById(R.id.etKondisiAwalUang);
                etKondisiAwalUangEK.addTextChangedListener(onTextChangedListener(etKondisiAwalUangEK));
                final EditText etKondisiAwalUtangEK = (EditText) editKondisiView.findViewById(R.id.etKondisiAwalUtang);
                etKondisiAwalUtangEK.addTextChangedListener(onTextChangedListener(etKondisiAwalUtangEK));
                final EditText etKondisiAwalSaldoEK = (EditText) editKondisiView.findViewById(R.id.etKondisiAwalSaldo);
                etKondisiAwalSaldoEK.addTextChangedListener(onTextChangedListener(etKondisiAwalSaldoEK));
                final EditText etKondisiAwalLabaEK = (EditText) editKondisiView.findViewById(R.id.etKondisiAwalLaba);
                etKondisiAwalLabaEK.addTextChangedListener(onTextChangedListener(etKondisiAwalLabaEK));
                etKondisiAwalUangEK.setText(this.tvUangNominal.getText().toString());
                etKondisiAwalUtangEK.setText(this.tvUtangNominal.getText().toString());
                etKondisiAwalSaldoEK.setText(this.tvSaldoNominal.getText().toString());
                etKondisiAwalLabaEK.setText(this.tvLabaNominal.getText().toString());
                ((Button) editKondisiView.findViewById(R.id.bKonfirmasi)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        if (etKondisiAwalUangEK.getText().toString().matches("")) {
                            etKondisiAwalUangEK.setText("0");
                        }
                        if (etKondisiAwalSaldoEK.getText().toString().matches("")) {
                            etKondisiAwalSaldoEK.setText("0");
                        }
                        if (etKondisiAwalLabaEK.getText().toString().matches("")) {
                            etKondisiAwalLabaEK.setText("0");
                        }
                        if (etKondisiAwalUtangEK.getText().toString().matches("")) {
                            etKondisiAwalUtangEK.setText("0");
                        }
                        MainActivity.this.tvUangNominal.setText(etKondisiAwalUangEK.getText());
                        MainActivity.this.tvSaldoNominal.setText(etKondisiAwalSaldoEK.getText());
                        MainActivity.this.tvLabaNominal.setText(etKondisiAwalLabaEK.getText());
                        MainActivity.this.tvUtangNominal.setText(etKondisiAwalUtangEK.getText());
                        dialogEditKondisi.dismiss();
                    }
                });
                break;
            case R.id.mAboutUs:
                AlertDialog.Builder aboutUs = new AlertDialog.Builder(this);
                View aboutUsView = getLayoutInflater().inflate(R.layout.layout_about_us, (ViewGroup) null);
                aboutUs.setView(aboutUsView);
                AlertDialog dialog = aboutUs.create();
                aboutUsView.setPadding(30, 30, 30, 30);
                dialog.show();
                ((Button) aboutUsView.findViewById(R.id.bYoutubeLink)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        MainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://bit.ly/pulsamanagergold")));
                    }
                });
                ((Button) aboutUsView.findViewById(R.id.bKembali)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        MainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://www.instagram.com/mochammad.hafidz/")));
                    }
                });
                break;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("log.txt", 0));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    /* access modifiers changed from: private */
    public void writeToFileTambahUang(String data, Context context) {
        try {
            String copiedData = readFromFile(this);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("log.txt", 0));
            outputStreamWriter.write("Tanggal & Waktu  : " + new SimpleDateFormat("dd/MM/yyyy : HH:mm").format(new Date()));
            outputStreamWriter.append("\r\n");
            outputStreamWriter.write("Uang ditambahkan : ");
            outputStreamWriter.write(data);
            outputStreamWriter.append("\r\n");
            outputStreamWriter.append("\r\n");
            outputStreamWriter.close();
            OutputStreamWriter outputStreamWriterAppend = new OutputStreamWriter(context.openFileOutput("log.txt", 32768));
            outputStreamWriterAppend.write(copiedData);
            outputStreamWriterAppend.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    /* access modifiers changed from: private */
    public void writeToFilePengambilanLaba(String data, String keterangan, Context context) {
        try {
            String copiedData = readFromFile(this);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("log.txt", 0));
            outputStreamWriter.write("Tanggal & Waktu  : " + new SimpleDateFormat("dd/MM/yyyy : HH:mm").format(new Date()));
            outputStreamWriter.append("\r\n");
            outputStreamWriter.write("Keterangan : ");
            outputStreamWriter.write(keterangan);
            outputStreamWriter.append("\r\n");
            outputStreamWriter.write("Pengambilan Laba : ");
            outputStreamWriter.write(data);
            outputStreamWriter.append("\r\n");
            outputStreamWriter.append("\r\n");
            outputStreamWriter.close();
            OutputStreamWriter outputStreamWriterAppend = new OutputStreamWriter(context.openFileOutput("log.txt", 32768));
            outputStreamWriterAppend.write(copiedData);
            outputStreamWriterAppend.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    /* access modifiers changed from: private */
    public void writeToFilePembelianSaldo(String data, Context context) {
        try {
            String copiedData = readFromFile(this);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("log.txt", 0));
            outputStreamWriter.write("Tanggal & Waktu  : " + new SimpleDateFormat("dd/MM/yyyy : HH:mm").format(new Date()));
            outputStreamWriter.append("\r\n");
            outputStreamWriter.write("Pembelian Saldo : ");
            outputStreamWriter.write(data);
            outputStreamWriter.append("\r\n");
            outputStreamWriter.append("\r\n");
            outputStreamWriter.close();
            OutputStreamWriter outputStreamWriterAppend = new OutputStreamWriter(context.openFileOutput("log.txt", 32768));
            outputStreamWriterAppend.write(copiedData);
            outputStreamWriterAppend.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    /* access modifiers changed from: private */
    public void writeToFilePembayaranUtang(String data, Context context) {
        try {
            String copiedData = readFromFile(this);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("log.txt", 0));
            outputStreamWriter.write("Tanggal & Waktu  : " + new SimpleDateFormat("dd/MM/yyyy : HH:mm").format(new Date()));
            outputStreamWriter.append("\r\n");
            String[] dataSplit = data.split("\\s+");
            if (dataSplit.length == 3) {
                outputStreamWriter.write(dataSplit[0] + " " + dataSplit[1] + " " + dataSplit[2]);
            } else if (dataSplit.length == 2) {
                outputStreamWriter.write(dataSplit[0] + " " + dataSplit[1]);
            } else {
                outputStreamWriter.write(dataSplit[0]);
            }
            outputStreamWriter.write(" Membayar Hutang ");
            outputStreamWriter.append("\r\n");
            outputStreamWriter.append("\r\n");
            outputStreamWriter.close();
            OutputStreamWriter outputStreamWriterAppend = new OutputStreamWriter(context.openFileOutput("log.txt", 32768));
            outputStreamWriterAppend.write(copiedData);
            outputStreamWriterAppend.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    /* access modifiers changed from: private */
    public void writeToFilePembayaranCicil(String data, String nominal, Context context) {
        try {
            String copiedData = readFromFile(this);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("log.txt", 0));
            outputStreamWriter.write("Tanggal & Waktu  : " + new SimpleDateFormat("dd/MM/yyyy : HH:mm").format(new Date()));
            outputStreamWriter.append("\r\n");
            String[] dataSplit = data.split("\\s+");
            if (dataSplit.length == 3) {
                outputStreamWriter.write(dataSplit[0] + " " + dataSplit[1] + " " + dataSplit[2]);
            } else if (dataSplit.length == 2) {
                outputStreamWriter.write(dataSplit[0] + " " + dataSplit[1]);
            } else {
                outputStreamWriter.write(dataSplit[0]);
            }
            outputStreamWriter.write(" Mencicil Hutang " + nominal);
            outputStreamWriter.append("\r\n");
            outputStreamWriter.append("\r\n");
            outputStreamWriter.close();
            OutputStreamWriter outputStreamWriterAppend = new OutputStreamWriter(context.openFileOutput("log.txt", 32768));
            outputStreamWriterAppend.write(copiedData);
            outputStreamWriterAppend.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    /* access modifiers changed from: private */
    public void writeToFileTambahNamaPenghutang(String nama, Context context) {
        try {
            if (!nama.isEmpty()) {
                String copiedData = readNamaFromFile(this);
                String[] copiedDataArray = copiedData.split("\\r?\\n");
                int i = 0;
                while (i < copiedDataArray.length) {
                    if (!nama.equals(copiedDataArray[i])) {
                        i++;
                    } else {
                        return;
                    }
                }
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("namaPenghutang.txt", 0));
                outputStreamWriter.write(nama);
                outputStreamWriter.append("\r\n");
                outputStreamWriter.close();
                OutputStreamWriter outputStreamWriterAppend = new OutputStreamWriter(context.openFileOutput("namaPenghutang.txt", 32768));
                outputStreamWriterAppend.write(copiedData);
                outputStreamWriterAppend.close();
            }
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    /* access modifiers changed from: private */
    public void writeToFileTambahHutang(String data, String nama, String saldo, String nomorHp, String keterangan, Context context) {
        if (Integer.parseInt(data.replace(",", "")) != 0) {
            try {
                String copiedData = readFromFile(this);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("log.txt", 0));
                outputStreamWriter.write("Tanggal & Waktu  : " + new SimpleDateFormat("dd/MM/yyyy : HH:mm").format(new Date()));
                outputStreamWriter.append("\r\n");
                outputStreamWriter.append("Nama Pembeli : " + nama);
                outputStreamWriter.append("\r\n");
                outputStreamWriter.append("Nomor HP : " + nomorHp);
                outputStreamWriter.append("\r\n");
                outputStreamWriter.write("Hutang ditambahkan  : ");
                outputStreamWriter.write(data);
                outputStreamWriter.append("\r\n");
                outputStreamWriter.append("Saldo dikurangi : ");
                outputStreamWriter.append(saldo);
                outputStreamWriter.append("\r\n");
                outputStreamWriter.append("Keterangan : ");
                outputStreamWriter.append(keterangan);
                outputStreamWriter.append("\r\n");
                outputStreamWriter.append("\r\n");
                outputStreamWriter.close();
                OutputStreamWriter outputStreamWriterAppend = new OutputStreamWriter(context.openFileOutput("log.txt", 32768));
                outputStreamWriterAppend.write(copiedData);
                outputStreamWriterAppend.close();
            } catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }
    }

    /* access modifiers changed from: private */
    public void writeToFileTambahBeli(String data, String nama, String saldo, String nomorHp, String keterangan, Context context) {
        if (Integer.parseInt(data.replace(",", "")) != 0) {
            try {
                String copiedData = readFromFile(this);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("log.txt", 0));
                outputStreamWriter.write("Tanggal & Waktu  : " + new SimpleDateFormat("dd/MM/yyyy : HH:mm").format(new Date()));
                outputStreamWriter.append("\r\n");
                outputStreamWriter.append("Nama Pembeli : " + nama);
                outputStreamWriter.append("\r\n");
                outputStreamWriter.write("Nomor HP  : ");
                outputStreamWriter.write(nomorHp);
                outputStreamWriter.append("\r\n");
                outputStreamWriter.write("Uang ditambahkan  : ");
                outputStreamWriter.write(data);
                outputStreamWriter.append("\r\n");
                outputStreamWriter.append("Saldo dikurangi : ");
                outputStreamWriter.append(saldo);
                outputStreamWriter.append("\r\n");
                outputStreamWriter.append("Keterangan : ");
                outputStreamWriter.append(keterangan);
                outputStreamWriter.append("\r\n");
                outputStreamWriter.append("\r\n");
                outputStreamWriter.close();
                OutputStreamWriter outputStreamWriterAppend = new OutputStreamWriter(context.openFileOutput("log.txt", 32768));
                outputStreamWriterAppend.write(copiedData);
                outputStreamWriterAppend.close();
            } catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }
    }

    /* access modifiers changed from: private */
    public void hapusLog(Context context) {
        try {
            new OutputStreamWriter(context.openFileOutput("log.txt", 0)).write("");
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    /* access modifiers changed from: private */
    public String readFromFile(Context context) {
        new StringBuffer();
        try {
            InputStream inputStream = context.openFileInput("log.txt");
            if (inputStream == null) {
                return "";
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            while (true) {
                String receiveString = bufferedReader.readLine();
                if (receiveString != null) {
                    stringBuilder.append(receiveString);
                    stringBuilder.append("\r\n");
                } else {
                    inputStream.close();
                    return stringBuilder.toString();
                }
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
            return "";
        } catch (IOException e2) {
            Log.e("login activity", "Can not read file: " + e2.toString());
            return "";
        }
    }

    /* access modifiers changed from: private */
    public String readNamaFromFile(Context context) {
        new StringBuffer();
        try {
            InputStream inputStream = context.openFileInput("namaPenghutang.txt");
            if (inputStream == null) {
                return "";
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            while (true) {
                String receiveString = bufferedReader.readLine();
                if (receiveString != null) {
                    stringBuilder.append(receiveString);
                    stringBuilder.append("\r\n");
                } else {
                    inputStream.close();
                    return stringBuilder.toString();
                }
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
            return "";
        } catch (IOException e2) {
            Log.e("login activity", "Can not read file: " + e2.toString());
            return "";
        }
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mLaporanLabaRugi:
                this.llrLabaKotor = this.llrUangMasuk - this.llrSaldoKeluar;
                this.llrHasil = (this.llrUangMasuk - this.llrSaldoKeluar) - this.llrPengeluaranLaba;
                AlertDialog.Builder llrBuilder = new AlertDialog.Builder(this);
                View llrView = getLayoutInflater().inflate(R.layout.layout_llr, (ViewGroup) null);
                llrBuilder.setView(llrView);
                ((TextView) llrView.findViewById(R.id.llrtvHasilNominal)).setText(Integer.toString(this.llrHasil));
                ((TextView) llrView.findViewById(R.id.llrtvUangMasukNominal)).setText(Integer.toString(this.llrUangMasuk));
                ((TextView) llrView.findViewById(R.id.llrtvLabaKotorNominal)).setText(Integer.toString(this.llrLabaKotor));
                ((TextView) llrView.findViewById(R.id.llrtvSaldoKeluarNominal)).setText(Integer.toString(this.llrSaldoKeluar));
                ((TextView) llrView.findViewById(R.id.llrtvPengeluaranLabaNominal)).setText(Integer.toString(this.llrPengeluaranLaba));
                final AlertDialog dialogLLR = llrBuilder.create();
                dialogLLR.show();
                ((Button) llrView.findViewById(R.id.llrbKembali)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        dialogLLR.dismiss();
                    }
                });
                ((Button) llrView.findViewById(R.id.llrbHapus)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle((CharSequence) "Konfirmasi");
                        builder.setMessage((CharSequence) "Apakah anda yakin ingin menghapus Laporan Laba Rugi?");
                        builder.setPositiveButton((CharSequence) "Iya", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.this.llrUangMasuk = 0;
                                MainActivity.this.llrSaldoKeluar = 0;
                                MainActivity.this.llrPengeluaranLaba = 0;
                                dialog.dismiss();
                                dialogLLR.dismiss();
                            }
                        });
                        builder.setNegativeButton((CharSequence) "Tidak", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    }
                });
                return true;
            default:
                return true;
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1000:
                if (grantResults[0] == 0) {
                    Toast.makeText(this, "Permission Granted", 0).show();
                    return;
                } else {
                    Toast.makeText(this, "Permission Not Granted", 0).show();
                    return;
                }
            default:
                return;
        }
    }

    /* access modifiers changed from: private */
    public void saveTextAsFile(String log) {
        try {
            FileOutputStream fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "pulsamanagerlog_" + new SimpleDateFormat("ddMMyyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".txt"));
            fos.write(log.getBytes());
            fos.close();
            Toast.makeText(this, "File Tersimpan di penyimpanan internal", 0).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Allow Permission, lalu tekan ulang tombol simpan", 0).show();
        } catch (IOException e2) {
            e2.printStackTrace();
            Toast.makeText(this, "Penyimpanan Error", 0).show();
        }
    }

    public static String currencyFormat(String amount) {
        return new DecimalFormat("###,###,###", new DecimalFormatSymbols(Locale.ENGLISH)).format(Double.parseDouble(amount));
    }

    /* access modifiers changed from: private */
    @Contract("_ -> !null")
    public TextWatcher onTextChangedListener(final EditText editText) {
        return new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                editText.removeTextChangedListener(this);
                try {
                    String originalString = s.toString();
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    editText.setText(new DecimalFormat("###,###,###", new DecimalFormatSymbols(Locale.ENGLISH)).format(Long.valueOf(Long.parseLong(originalString))));
                    editText.setSelection(editText.getText().length());
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
                editText.addTextChangedListener(this);
            }
        };
    }
}
