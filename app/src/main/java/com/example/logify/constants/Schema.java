package com.example.logify.constants;

public class Schema {
    public static final String ALBUMS = "albums";
    public static final String ARTISTS = "artists";
    public static final String USERS = "users";
    public static final String SONGS = "songs";
    public static final String PLAYLISTS = "playlists";
    public static final String TOPICS = "topics";
    public static final String FAVORITE_ALBUMS = "favoriteAlbums";
    public static final String FAVORITE_SONGS = "favoriteSongs";
    public static final String FAVORITE_ARTISTS = "favoriteArtists";
    public static final String PRIVATE_PLAYLISTS = "privatePlaylists";
    public static final String SONGS_UPLOADED = "songsUploaded";

    public static class SongType {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String DURATION = "duration";
        public static final String IMAGE = "imageResource";
        public static final String ARTIST_ID = "artistId";
        public static final String RELEASE_DATE = "releaseDate";
        public static final String IMAGE_RESOURCE = "imageResource";
        public static final String RESOURCE = "resource";
    }

    public static class PlaylistType {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String IMAGE = "image";
        public static final String DESCRIPTION = "description";
        public static final String SONGS = "favoriteSongs";
        public static final String CREATED_DATE = "createdDate";
    }

    public static class ArtistType {
        public static final String ID = "artistId";
        public static final String NAME = "artistName";
        public static final String IMAGE = "image";
        public static final String PLAYLIST_ID = "playlistId";
    }
}
