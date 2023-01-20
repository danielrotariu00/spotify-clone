import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { Button } from "primereact/button";
import { Card } from "primereact/card";
import { Link } from "react-router-dom";
import useToken from "../../hooks/useToken";

export default function PlaylistDetails({ userId }) {
  const { token, setToken } = useToken();
  const params = useParams();
  const initialValue = {
    id: 0,
    name: "",
    songs: [],
  };
  const [playlist, setPlaylist] = useState(initialValue);

  useEffect(() => {
    getPlaylist(params.id);
  }, []);

  const onDeleteClick = (songId) => {
    const requestOptions = {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    };
    fetch(
      `http://localhost:8082/api/users/${userId}/playlists/${playlist.id}/songs/${songId}`,
      requestOptions
    )
      .then(() => window.location.reload(true))
      .catch((err) => {
        console.log(err.message);
      });
  };

  const getPlaylist = (id) => {
    let url = `http://localhost:8082/api/users/${userId}/playlists/${id}`;
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
        setPlaylist(data);
      })
      .catch((err) => {
        console.log(err.message);
      });
  };

  return (
    <div>
      <h3>Id: {playlist.id}</h3>
      <h3>Name: {playlist.name}</h3>
      <h3>Songs:</h3>
      <ul>
        {playlist.songs.map((song) => (
          <div>
            <Card
              title={<Link to={`/songs/${song.id}`}>{song.name}</Link>}
              style={{ height: "8em", width: "20em" }}
            >
              <Button label="Delete" onClick={() => onDeleteClick(song.id)} />
            </Card>
            <br />
          </div>
        ))}
      </ul>
    </div>
  );
}
