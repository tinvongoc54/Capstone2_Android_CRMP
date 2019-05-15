package tweather.framgia.com.crimeandmissingreport.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import tweather.framgia.com.crimeandmissingreport.R;

public class AboutUsFragment extends Fragment {

    ImageView mImageViewTin, mImageViewQuy, mImageViewThang, mImageViewThuan;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mImageViewTin = view.findViewById(R.id.imageViewTin);
        Picasso.get()
                .load("https://scontent.fdad1-1.fna.fbcdn.net/v/t1.0-9/57410717_1" +
                        "672089236268228_1711446391877795840_n.jpg?_nc_cat=100&_nc_oc=AQnV4fzkzRV8B" +
                        "MriCK8hWVSrpgZwwtqSlukZdp0nQtxtptLxqa2XAUtZZoNYDg4ProhKEG_BoQvjV-QVS_OvPXqx&_nc_ht=scontent.fdad1-1.fna&oh=b0feba16c9ed8da9c55bd269c9bab6a8&oe=5D68DA20")
                .into(mImageViewTin);
        mImageViewQuy = view.findViewById(R.id.imageViewQuy);
        Picasso.get()
                .load("https://scontent.fdad1-1.fna.fbcdn.net/v/t1.0-9/51777824_181" +
                        "4472431991007_2904882993002708992_n.jpg?_nc_cat=104&_nc_oc=AQkJChbsZ" +
                        "FjAlsPAFMdzznG9I5rtm8mGsU8A_AEpD_scu42gjTAtXCy-pJ9xxexxskXTgh9J_LPTupebgz" +
                        "UvNrJ0&_nc_ht=scontent.fdad1-1.fna&oh=9d67427d3af3fff41750e51d0d155a83&oe=5D75EE95")
                .into(mImageViewQuy);
        mImageViewThang = view.findViewById(R.id.imageViewThang);
        Picasso.get()
                .load("https://scontent.fdad1-1.fna.fbcdn.net/v/t1.0-9/52444278_235489" +
                        "1311202353_7147482654705713152_n.jpg?_nc_cat=100&_nc_oc=AQlIQPLqKvaxb-vOw" +
                        "SwedVCCKMW6kBCWQOn6QQYO3vuYFILtv8bsxRJK9aRGPjEOf58WBJ2bgQ6-a4Qz_2YOYqa7&_nc_ht=s" +
                        "content.fdad1-1.fna&oh=c79e184b0ad5ad5a733690914861c01b&oe=5D6FD5BB")
                .into(mImageViewThang);
        mImageViewThuan = view.findViewById(R.id.imageViewThuan);
        Picasso.get()
                .load("https://scontent.fdad1-1.fna.fbcdn.net/v/t1.0-9/51924594_1" +
                        "510638919069053_9096661069874593792_n.jpg?_nc_cat=105&_nc_oc=AQm4T9VKX3vL0rr-VX" +
                        "8-opByf7iXI9conZ6-OYYr0lwd7LQQ-ktCEciQ6EvQk3bmg4pSHnkBRBNVS3Cb0TAcHdBc&_nc_ht=scont" +
                        "ent.fdad1-1.fna&oh=6aaf00d9689595e3282e8367dfeedcc4&oe=5D64EBD1")
                .into(mImageViewThuan);
    }
}
