package com.doctor.app.doctorapp.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.doctor.app.doctorapp.Model.DoctorInfo;
import com.doctor.app.doctorapp.R;
import com.doctor.app.doctorapp.Utilitario.Common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getName();
    private String key_Uid;
    private Query qDoctor;
    private DatabaseReference dbrDoctor;

    public static Fragment createInstance(String valor) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(Common.TAG_KEY_UID, valor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        key_Uid = getArguments().getString(Common.TAG_KEY_UID);
        dbrDoctor = FirebaseDatabase.getInstance().getReference(Common.TABLE_DOCTOR_INFO);
        qDoctor = dbrDoctor.orderByKey().equalTo(key_Uid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_main, container, false);

        final TextView nombre = view.findViewById(R.id.txtName_user);
        final TextView apellido = view.findViewById(R.id.txtLastName_user);
        final TextView celular = view.findViewById(R.id.txtPhone_user);

        qDoctor.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    DoctorInfo user = snapshot.getValue(DoctorInfo.class);
                    nombre.setText(user.getName());
                    apellido.setText(user.getLastName());
                    celular.setText(String.valueOf(user.getPhone()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG,databaseError.getMessage());
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        Log.i(TAG,"onResume");
        super.onResume();
    }

    @Override
    public void onStart() {
        Log.i(TAG,"onStart");
        super.onStart();
    }

    @Override
    public void onStop() {
        Log.i(TAG,"onStop");
        super.onStop();
    }

}
