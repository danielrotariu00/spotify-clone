import React, { useState, useEffect } from "react";
import { Card } from "primereact/card";
import { Button } from "primereact/button";
import { Splitter, SplitterPanel } from "primereact/splitter";
import { InputText } from "primereact/inputtext";
import { Link } from "react-router-dom";
import useToken from "../../hooks/useToken";

export default function Playlists({ userId }) {
  const { token, setToken } = useToken();
  const [playlists, setPlaylists] = useState([]);
  const [name, setName] = useState(null);
  const [newName, setNewName] = useState(null);

  useEffect(() => {
    getPlaylists();
  }, []);

  const onFilterClick = () => {
    getPlaylists();
  };

  const onCreateClick = () => {
    const requestOptions = {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ name: newName }),
    };
    fetch(`http://localhost:8082/api/users/${userId}/playlists`, requestOptions)
      .then((response) => response.json())
      .then((_data) => getPlaylists());
  };

  const onDeleteClick = (playlistId) => {
    const requestOptions = {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    };
    fetch(
      `http://localhost:8082/api/users/${userId}/playlists/${playlistId}`,
      requestOptions
    ).then(() => getPlaylists());
  };

  const getPlaylists = () => {
    let url = `http://localhost:8082/api/users/${userId}/playlists`;

    if (name !== null) {
      url += `?name=${name}`;
    }

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

  return (
    <div>
      <Splitter
        style={{
          height: "93.5vh",
          backgroundColor: "black",
        }}
        layout="horizontal"
      >
        <SplitterPanel className="flex align-items-center justify-content-center">
          <h2>New Playlist</h2>
          <h3>Name</h3>
          <InputText
            placeholder="Name"
            onChange={(e) => setNewName(e.target.value)}
          />
          <br />
          <br />
          <Button label="Create" onClick={onCreateClick} />
        </SplitterPanel>
        <SplitterPanel
          style={{ overflowY: "scroll" }}
          className="flex align-items-center justify-content-center"
        >
          <h2>Filters</h2>
          <h3>Name</h3>
          <InputText
            placeholder="Name"
            onChange={(e) => setName(e.target.value)}
          />
          <br />
          <br />
          <Button label="Filter playlists" onClick={onFilterClick} />
          <br />
          <br />
          <h2>Playlists</h2>
          <ul>
            {playlists.map((playlist) => (
              <div>
                <Card
                  title={
                    <Link to={`/playlists/${playlist.id}`}>
                      {playlist.name}
                    </Link>
                  }
                  style={{ height: "8em", width: "20em" }}
                >
                  <Button
                    label="Delete"
                    onClick={() => {
                      onDeleteClick(playlist.id);
                    }}
                  />
                </Card>
                <br />
              </div>
            ))}
          </ul>
        </SplitterPanel>
      </Splitter>
    </div>
  );
}
