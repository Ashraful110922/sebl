package modal;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.opl.one.oplsales.R;

public class ErrorMessageModal extends BottomSheetDialogFragment {
    private Context context;
    private Bundle bundle;
    private TextView showError;
    private Button btnDismiss;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.error_meaage,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = this.getActivity();
        bundle = this.getArguments();
        intUi();
    }

    private void intUi() {
        showError = (TextView) getView().findViewById(R.id.showError);
        btnDismiss = (Button) getView().findViewById(R.id.btnDismiss);

        if (bundle!=null)
        showError.setText(bundle.getString("ERROR_MESSAGE"));
        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

    }
}
