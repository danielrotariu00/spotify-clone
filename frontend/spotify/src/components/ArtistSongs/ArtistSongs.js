import React, { useState, useEffect } from "react";
import { Card } from "primereact/card";
import { Button } from "primereact/button";
import { Splitter, SplitterPanel } from "primereact/splitter";
import { RadioButton } from "primereact/radiobutton";
import { InputText } from "primereact/inputtext";
import { InputNumber } from "primereact/inputnumber";
import { ListBox } from "primereact/listbox";
import useUserId from "../../hooks/useUserId";
import useToken from "../../hooks/useToken";

export default function ArtistSongs() {
  const { userId, setUserId } = useUserId();
  const { token, setToken } = useToken();

  const [artistSongs, setArtistSongs] = useState([]);
  const [selectedSong, setSelectedSong] = useState(null);
  const [newName, setNewName] = useState(null);
  const [newYear, setNewYear] = useState(null);

  const genres = [
    { name: "METAL", value: "METAL" },
    { name: "ROCK", value: "ROCK" },
    { name: "RAP", value: "RAP" },
  ];
  const [newSelectedGenre, setNewSelectedGenre] = useState(genres[0]);

  const types = [
    { name: "SONG", value: "SONG" },
    { name: "ALBUM", value: "ALBUM" },
  ];
  const [newSelectedType, setNewSelectedType] = useState(types[0]);
  const [newSelectedParent, setNewSelectedParent] = useState(null);

  useEffect(() => {
    getArtistSongs();
  });

  const getArtistSongs = () => {
    let url = `http://localhost:8081/api/songcollection/artists/${userId}/songs`;

    fetch(url)
      .then((response) => response.json())
      .then((data) => {
        setArtistSongs(data);
      })
      .catch((err) => {
        console.log(err.message);
      });
  };

  const onCreateClick = () => {
    const requestOptions = {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        name: newName,
        genre: newSelectedGenre.value,
        year: newYear,
        type: newSelectedType.value,
        parent: newSelectedParent?.id,
      }),
    };
    fetch(`http://localhost:8081/api/songcollection/songs`, requestOptions)
      .then((response) => response.json())
      .then((_data) => getArtistSongs());
  };

  const onDeleteClick = (songId) => {
    const requestOptions = {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    };
    fetch(
      `http://localhost:8081/api/songcollection/songs/${songId}`,
      requestOptions
    ).then(() => getArtistSongs());
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
          <h2>Create New Song</h2>
          <h3>Name</h3>
          <InputText
            placeholder="Name"
            onChange={(e) => setNewName(e.target.value)}
          />
          <h3>Genre</h3>
          {genres.map((genre) => {
            return (
              <div key={genre.name} className="field-radiobutton">
                <RadioButton
                  inputId={genre.name}
                  name="genre"
                  value={genre}
                  onChange={(e) => setNewSelectedGenre(e.value)}
                  checked={newSelectedGenre.name === genre.name}
                />
                <label htmlFor={genre.name}>{genre.name}</label>
              </div>
            );
          })}
          <h3>Year</h3>
          <InputNumber
            placeholder="Year"
            onChange={(e) => setNewYear(e.value)}
          />
          <h3>Type</h3>
          {types.map((type) => {
            return (
              <div key={type.name} className="field-radiobutton">
                <RadioButton
                  inputId={type.name}
                  name="type"
                  value={type}
                  onChange={(e) => setNewSelectedType(e.value)}
                  checked={newSelectedType.name === type.name}
                />
                <label htmlFor={type.name}>{type.name}</label>
              </div>
            );
          })}
          <h3>Parent</h3>
          <ListBox
            value={newSelectedParent}
            options={artistSongs.filter((song) => song.type === "ALBUM")}
            onChange={(e) => setNewSelectedParent(e.value)}
            optionLabel="name"
            style={{ width: "15rem" }}
          />
          <br />
          <br />
          <Button label="Create" onClick={onCreateClick} />
        </SplitterPanel>
        <SplitterPanel
          style={{ overflowY: "scroll" }}
          className="flex align-items-center justify-content-center"
        >
          <h2>Song List</h2>
          <ul>
            {artistSongs.map((song) => (
              <div>
                <Card
                  title={song.name}
                  subTitle={song.genre}
                  style={{ height: "10em", width: "20em" }}
                >
                  <div>
                    <Button
                      label="View Details"
                      onClick={() => {
                        setSelectedSong(song);
                      }}
                    />
                    &emsp;
                    <Button
                      label="Delete"
                      onClick={() => {
                        onDeleteClick(song.id);
                      }}
                    />
                  </div>
                </Card>
                <br />
              </div>
            ))}
          </ul>
        </SplitterPanel>
        <SplitterPanel className="flex align-items-center justify-content-center">
          <h2>Song Details</h2>
          <h3>Id: {selectedSong?.id}</h3>
          <h3>Name: {selectedSong?.name}</h3>
          <h3>Genre: {selectedSong?.genre}</h3>
          <h3>Year: {selectedSong?.year}</h3>
          <h3>Type: {selectedSong?.type}</h3>
          <h3>Parent: {selectedSong?.parent}</h3>
        </SplitterPanel>
      </Splitter>
    </div>
  );
}
