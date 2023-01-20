import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { Button } from "primereact/button";
import { Dialog } from "primereact/dialog";
import { ListBox } from "primereact/listbox";
import useToken from "../../hooks/useToken";

export default function SongDetails({ userId }) {
  const params = useParams();
  const initialValue = {
    id: 0,
    name: "",
    genre: "",
    year: 0,
    type: "",
    parent: null,
  };
  const [song, setSong] = useState(initialValue);
  const { token, setToken } = useToken();
  const [playlists, setPlaylists] = useState([]);
  const [displayPlaylists, setDisplayPlaylists] = useState(false);
  const [selectedPlaylist, setSelectedPlaylist] = useState(null);

  useEffect(() => {
    getSong(params.id);
    getPlaylists();
  }, []);

  const onAddClick = () => {
    setDisplayPlaylists(true);
  };

  const getSong = (id) => {
    let url = `http://localhost:8081/api/songcollection/songs/${id}`;

    fetch(url)
      .then((response) => response.json())
      .then((data) => {
        setSong(data);
      })
      .catch((err) => {
        console.log(err.message);
      });
  };

  const getPlaylists = () => {
    let url = `http://localhost:8082/api/users/${userId}/playlists`;

    fetch(url, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((response) => {
        if (response.status === 401) {
          setToken(null);
        }
        if (!response.ok) {
          throw new Error(response.status);
        }
        return response.json();
      })
      .then((data) => {
        setPlaylists(data);
      })
      .catch((err) => {
        console.log(err.message);
      });
  };

  const onHide = () => {
    setDisplayPlaylists(false);
  };

  const onConfirmClick = () => {
    const requestOptions = {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    };
    fetch(
      `http://localhost:8082/api/users/${userId}/playlists/${selectedPlaylist.id}/songs/${song.id}`,
      requestOptions
    ).catch((err) => {
      console.log(err.message);
    });
  };

  const renderFooter = () => {
    return (
      <div>
        <Button
          label="Cancel"
          icon="pi pi-times"
          onClick={() => onHide()}
          className="p-button-text"
        />
        <Button
          label="Add"
          icon="pi pi-check"
          onClick={() => {
            onConfirmClick();
            onHide();
          }}
          autoFocus
          disabled={playlists.length == 0}
        />
      </div>
    );
  };

  return (
    <div>
      <h3>Id: {song.id}</h3>
      <h3>Name: {song.name}</h3>
      <h3>Genre: {song.genre}</h3>
      <h3>Year: {song.year}</h3>
      <h3>Type: {song.type}</h3>
      <h3>Parent: {song.parent}</h3>
      <Button label="Add to Playlist" onClick={onAddClick} />
      <Dialog
        header="Select Playlist"
        visible={displayPlaylists}
        style={{ width: "50vw" }}
        footer={renderFooter()}
        onHide={() => onHide()}
      >
        <ListBox
          value={selectedPlaylist}
          options={playlists}
          onChange={(e) => setSelectedPlaylist(e.value)}
          optionLabel="name"
          style={{ width: "15rem" }}
        />
      </Dialog>
    </div>
  );
}
