package interfac;


import androidx.fragment.app.Fragment;

public interface CommunicatorFragmentInterface {

     public void  setContentFragment(Fragment fragment, boolean addToBackStack);
     public void addContentFragment(Fragment fragment, boolean addToBackStack);
     public void removeAllFragment();




}
