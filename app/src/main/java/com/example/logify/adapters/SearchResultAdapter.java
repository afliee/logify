package com.example.logify.adapters;

import android.app.appsearch.SearchResult;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.logify.R;
import com.example.logify.entities.Artist;
import com.example.logify.entities.Playlist;
import com.example.logify.entities.Song;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class SearchResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_VIEW1 = 1;
    private static final int TYPE_VIEW2 = 2;
    private static final int TYPE_VIEW3 = 3;

        private ArrayList<Object> items;
        private Context context;

        public SearchResultAdapter(Context context) {
            this.context = context;
        }
        public SearchResultAdapter(Context context, ArrayList<Object> items) {
            this.context = context;
            this.items = (items);
        }

        @Override
        public int getItemViewType(int position) {
            Object item = items.get(position);
            if (item instanceof Playlist) {
                return TYPE_VIEW1;
            } else if (item instanceof Artist) {
                return TYPE_VIEW2;
            } else if(item instanceof Song){
                return TYPE_VIEW3;
            }
            return -1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder;
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

            switch (viewType) {
                case TYPE_VIEW1:
                    View v = layoutInflater.inflate(R.layout.library_playlist_item, parent, false);
                    viewHolder = new ViewHolderPlaylist(v);
                    break;
                case TYPE_VIEW2:
                    View v2 = layoutInflater.inflate(R.layout.library_artist_item, parent, false);
                    viewHolder = new ViewHolderArtist(v2);
                    break;
                case TYPE_VIEW3:
                    View v3 = layoutInflater.inflate(R.layout.search_result_song_item, parent, false);
                    viewHolder = new ViewHolderSong(v3);
                    break;
                default:
                    viewHolder = null;
                    break;
            }

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case TYPE_VIEW1:
                    Playlist playlist = (Playlist) SearchResultAdapter.this.items.get(position);
                    SearchResultAdapter.ViewHolderPlaylist viewHolder1 = (ViewHolderPlaylist) holder;
                    viewHolder1.tvPlaylistName.setText(playlist.getTitle());
                    Glide.with(context).load(playlist.getImage()).into(viewHolder1.imvPlaylist);
                    break;
                case TYPE_VIEW2:
                    Artist artist = (Artist) SearchResultAdapter.this.items.get(position);
                    SearchResultAdapter.ViewHolderArtist viewHolder2 = (SearchResultAdapter.ViewHolderArtist) holder;
                    viewHolder2.tvArtistName.setText(artist.getName());
                    viewHolder2.tvTitle.setText("Artists");
                    Glide.with(context).load(artist.getImage()).into(viewHolder2.imvLibraryArtist);
                    break;
                case TYPE_VIEW3:
                    Song song = (Song) SearchResultAdapter.this.items.get(position);
                    SearchResultAdapter.ViewHolderSong viewHolder3 = (SearchResultAdapter.ViewHolderSong) holder;
                    viewHolder3.tvSongName.setText(song.getName());
                    viewHolder3.tvTitle.setText("Artists");
                    Glide.with(context).load(song.getImageResource()).into(viewHolder3.imvSong);
                    break;
                default:
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        private static class ViewHolderPlaylist extends RecyclerView.ViewHolder {
            private ImageView imvPlaylist;
            private TextView tvPlaylistName;
            private TextView tvTitle;
            private ConstraintLayout libPlaylistItem;
            public ViewHolderPlaylist(@NonNull View itemView) {
                super(itemView);
                imvPlaylist = itemView.findViewById(R.id.imvPlaylist);
                tvPlaylistName = itemView.findViewById(R.id.tvPlaylistName);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvTitle.setText("Playlist");
                libPlaylistItem = itemView.findViewById(R.id.libPlaylistItem);
            }
        }

        private static class ViewHolderArtist extends RecyclerView.ViewHolder {
            private ImageView imvLibraryArtist;
            private TextView tvArtistName;
            private TextView tvTitle;
            private ConstraintLayout clLibraryArtist;

            public ViewHolderArtist(View itemView) {
                super(itemView);
                imvLibraryArtist = itemView.findViewById(R.id.imvArtist);
                tvArtistName = itemView.findViewById(R.id.tvArtistName);
                clLibraryArtist = itemView.findViewById(R.id.rcvArtist);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvTitle.setText("Artists");
            }
        }

        private static class ViewHolderSong extends RecyclerView.ViewHolder {
            private ShapeableImageView imvSong;
            private TextView tvSongName;
            private TextView tvTitle;
            private ConstraintLayout clLibraryArtist;

            public ViewHolderSong(View itemView) {
                super(itemView);
                imvSong = itemView.findViewById(R.id.image);
                tvSongName = itemView.findViewById(R.id.title);
                clLibraryArtist = itemView.findViewById(R.id.rcvSearchResult);
                tvTitle = itemView.findViewById(R.id.description);
                tvTitle.setText("Song");
            }
        }
}
