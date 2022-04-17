package com.google.android.youtube.player;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.google.android.youtube.player.internal.ab;

@SuppressWarnings("deprecation")
public class YouTubePlayerSupportFragmentX extends Fragment implements YouTubePlayer.Provider {
    private final YouTubePlayerSupportFragmentX.a a = new a();
    private Bundle b;
    private YouTubePlayerView c;
    private String d;
    private YouTubePlayer.OnInitializedListener e;
    private boolean f;

    public static YouTubePlayerSupportFragmentX newInstance() {
        return new YouTubePlayerSupportFragmentX();
    }

    public YouTubePlayerSupportFragmentX() {
        //Initiate YouTubePlayerSupportFragmentX
    }

    public void initialize(String var1, YouTubePlayer.OnInitializedListener var2) {
        this.d = ab.a(var1, "Developer key cannot be null or empty");
        this.e = var2;
        this.a();
    }

    private void a() {
        if (this.c != null && this.e != null) {
            this.c.a(this.f);
            this.c.a(this.getActivity(), this, this.d, this.e, this.b);
            this.b = null;
            this.e = null;
        }

    }

    @Override
    public void onCreate(Bundle var1) {
        super.onCreate(var1);
        this.b = var1 != null ? var1.getBundle("YouTubePlayerSupportFragment.KEY_PLAYER_VIEW_STATE") : null;
    }

    @Override
    public View onCreateView(LayoutInflater var1, ViewGroup var2, Bundle var3) {
        this.c = new YouTubePlayerView(this.getActivity(), null, 0, this.a);
        this.a();
        return this.c;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.c.a();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.c.b();
    }

    @Override
    public void onPause() {
        this.c.c();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle var1) {
        super.onSaveInstanceState(var1);
        Bundle var2 = this.c != null ? this.c.e() : this.b;
        var1.putBundle("YouTubePlayerSupportFragment.KEY_PLAYER_VIEW_STATE", var2);
    }

    @Override
    public void onStop() {
        this.c.d();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        this.c.c(this.getActivity().isFinishing());
        this.c = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (this.c != null) {
            FragmentActivity var1 = this.getActivity();
            this.c.b(var1 == null || var1.isFinishing());
        }

        super.onDestroy();
    }

    private final class a implements YouTubePlayerView.b {
        private a() {
        }

        public final void a(YouTubePlayerView var1, String var2, YouTubePlayer.OnInitializedListener var3) {
            YouTubePlayerSupportFragmentX.this.initialize(var2, YouTubePlayerSupportFragmentX.this.e);
        }

        public final void a(YouTubePlayerView var1) {
            //Empty function requires
        }
    }
}
