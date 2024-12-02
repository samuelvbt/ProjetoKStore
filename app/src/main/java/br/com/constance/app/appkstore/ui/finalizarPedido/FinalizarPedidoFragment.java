package br.com.constance.app.appkstore.ui.finalizarPedido;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.appkstore.R;
import com.example.appkstore.databinding.FragmentFinalizarPedidoBinding;
import com.google.android.material.snackbar.Snackbar;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import br.com.constance.app.appkstore.SessionManager;
import br.com.constance.app.appkstore.backend.controller.PedidoController;
import br.com.constance.app.appkstore.backend.services.pojo.PedidoInterfacePOJO;

public class FinalizarPedidoFragment extends Fragment {
//
    private FragmentFinalizarPedidoBinding binding;
    private RadioGroup radioGroupOptions;
    private EditText editTextDate;
    private final Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat;
    private Bundle bundle;
    private EditText editTextObservacao;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SessionManager sessionManager = SessionManager.getInstance(getContext());
        boolean isLoggedIn = sessionManager.isLoggedIn();
        if (!isLoggedIn) {
            NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
            NavController navController = navHostFragment.getNavController();
            navController.navigate(R.id.nav_login);
        }

        // Inflar o layout usando databinding
        binding = FragmentFinalizarPedidoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        View headerView = inflater.inflate(R.layout.nav_header_main, container, false);
        radioGroupOptions = root.findViewById(R.id.tipos_negociacao);
        editTextObservacao = root.findViewById(R.id.observacao);
        bundle=getArguments();
        String[] options = {"VENDA MM - PGTO 100% ANTECIPADO" ,"Venda MM - PRAZO 30/60/90", "MM - SHOWROOM 30/60/90/120", "MM - SHOWROOM 30/60/90/120/150", "VENDA MM - PRAZO 100%(30/60/90)"};
        int idCounter = 1;
        for (String option : options) {
            RadioButton radioButton = new RadioButton(getActivity());
            radioButton.setLayoutParams(new RadioGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            radioButton.setText(option);
            radioButton.setId(idCounter++);
            radioGroupOptions.addView(radioButton);
        }
        editTextDate = root.findViewById(R.id.editTextDate2);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel();
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        editTextDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Button buttonFinaliza = root.findViewById(R.id.finaliza);
        buttonFinaliza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String dateStr = editTextDate.getText().toString();
                Date datetime = null;
                if (!dateStr.isEmpty()&& isValidDate(dateStr)) {

                    try {
                        datetime = sdf.parse(dateStr);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }

                    PedidoController pedidoController = new PedidoController(getContext());
                    try {
                        if(pedidoController.obterTotalPedido(bundle.getString("idPedido")).compareTo(BigDecimal.ZERO)!=0) {
                            if (getTipoNegociacao(radioGroupOptions.getCheckedRadioButtonId()).compareTo(BigDecimal.ZERO) != 0) {
                                PedidoInterfacePOJO pedidoInterfacePOJO = new PedidoInterfacePOJO();
                                pedidoInterfacePOJO.setIdPedido(bundle.getString("idPedido"));
                                pedidoInterfacePOJO.setDataEntrega(datetime == null ? null : datetime.getTime());
                                pedidoInterfacePOJO.setCodigoTipoNegociacao(getTipoNegociacao(radioGroupOptions.getCheckedRadioButtonId()));
                                pedidoInterfacePOJO.setObservacao(editTextObservacao.getText().toString());

                                pedidoController.finalizarPedido(pedidoInterfacePOJO);
                                Snackbar.make(root, "Pedido Finalizado", Snackbar.LENGTH_SHORT).show();
                                Thread.sleep(2000);

                                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                fragmentManager.popBackStack();
                                fragmentManager.popBackStack();
                                fragmentManager.popBackStack();

                            } else {
                                Snackbar.make(root, "Preencha tipo de negociação", Snackbar.LENGTH_SHORT).show();
                            }
                        }else{
                            Snackbar.make(root,"O pedido não possui itens", Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                }



        });

        return root;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void updateLabel() {
        editTextDate.setText(dateFormat.format(calendar.getTime()));
    }

    private boolean isValidDate(String date) {
        try {
            dateFormat.setLenient(false);
            dateFormat.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    private BigDecimal getTipoNegociacao(int codigoNegociacao){
        switch(codigoNegociacao){
            case 1:
                return new BigDecimal(73);
            case 2:
                return new BigDecimal(77);
            case 3:
                return new BigDecimal(347);
            case 4:
                return new BigDecimal(348);
            case 5:
                return new BigDecimal(350);
            default :
                return new BigDecimal(0);
        }
    }
}