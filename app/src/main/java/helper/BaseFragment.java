package helper;


import android.content.Context;
import androidx.fragment.app.Fragment;
import interfac.CommunicatorFragmentInterface;


public abstract class BaseFragment extends Fragment {
    public CommunicatorFragmentInterface myCommunicator;
    private boolean openMenuOnBackPress=false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            myCommunicator = (CommunicatorFragmentInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        myCommunicator = null;
    }

    public boolean openMenuOnBackPress() {
        return openMenuOnBackPress;
    }

    public void setOpenMenuOnBackPress(boolean openMenuOnBackPress) {
        this.openMenuOnBackPress = openMenuOnBackPress;
    }

}
