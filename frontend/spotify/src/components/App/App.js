import { React, useEffect } from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import "./App.css";
import classes from "primereact/resources/primereact.css";
import theme from "primereact/resources/themes/bootstrap4-dark-blue/theme.css";
import "primeicons/primeicons.css";

import Menu from "../Menu/Menu";
import Login from "../Login/Login";
import Playlists from "../Playlists/Playlists";
import SongDetails from "../SongDetails/SongDetails";
import PlaylistDetails from "../PlaylistDetails/PlaylistDetails";
import Songs from "../Songs.js/Songs";
import Artists from "../Artists/Artists";
import ArtistDetails from "../ArtistDetails/ArtistDetails";
import useToken from "../../hooks/useToken";
import useUserId from "../../hooks/useUserId";
import useUserRoles from "../../hooks/useUserRoles";
import ArtistSongs from "../ArtistSongs/ArtistSongs";
import NewSong from "../NewSong/NewSong";
import NewArtist from "../NewArtist/NewArtist";
import Users from "../Users/Users";

function App() {
  const { token, setToken } = useToken();
  const { userId, setUserId } = useUserId();
  const { userRoles, setUserRoles } = useUserRoles();

  useEffect(() => {}, []);

  if (!token) {
    return (
      <Login
        setToken={setToken}
        setUserId={setUserId}
        setUserRoles={setUserRoles}
      />
    );
  }

  return (
    <div className="wrapper">
      <Menu token={token} userRoles={userRoles} />
      <BrowserRouter>
        <Routes>
          <Route path="/songs" element={<Songs />} />
          <Route path="/songs/:id" element={<SongDetails userId={userId} />} />
          <Route path="/artists" element={<Artists />} />
          <Route path="/artists/:id" element={<ArtistDetails />} />
          <Route path="/playlists" element={<Playlists userId={userId} />} />
          <Route
            path="/playlists/:id"
            element={<PlaylistDetails userId={userId} />}
          />
          <Route path="/artist-songs" element={<ArtistSongs />} />
          <Route path="/songs/new" element={<NewSong />} />
          <Route path="/artists/new" element={<NewArtist />} />
          <Route path="/users" element={<Users />} />
        </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;
