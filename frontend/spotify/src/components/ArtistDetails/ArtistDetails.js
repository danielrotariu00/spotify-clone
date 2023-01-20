import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { Button } from "primereact/button";
import { Card } from "primereact/card";
import { Link } from "react-router-dom";
import useToken from "../../hooks/useToken";

export default function ArtistDetails() {
  const params = useParams();
  const initialValue = {
    id: 0,
    name: "",
  };
  const [artist, setArtist] = useState(initialValue);
  const [artistSongs, setArtistSongs] = useState([]);

  useEffect(() => {
    getArtist(params.id);
    getArtistSongs(params.id);
  }, []);

  const getArtist = (id) => {
    let url = `http://localhost:8081/api/songcollection/artists/${id}`;
    fetch(url)
      .then((response) => response.json())
      .then((data) => {
        setArtist(data);
      })
      .catch((err) => {
        console.log(err.message);
      });
  };

  const getArtistSongs = (id) => {
    let url = `http://localhost:8081/api/songcollection/artists/${id}/songs`;
    fetch(url)
      .then((response) => response.json())
      .then((data) => {
        setArtistSongs(data);
      })
      .catch((err) => {
        console.log(err.message);
      });
  };

  return (
    <div>
      <h3>Id: {artist.id}</h3>
      <h3>Name: {artist.name}</h3>
      <h3>Songs:</h3>
      <ul>
        {artistSongs.map((song) => (
          <div>
            <Card
              title={<Link to={`/songs/${song.id}`}>{song.name}</Link>}
              style={{ height: "5em", width: "20em" }}
            ></Card>
            <br />
          </div>
        ))}
      </ul>
    </div>
  );
}
