import React, { useState } from "react";
import { Button } from "primereact/button";
import { Splitter, SplitterPanel } from "primereact/splitter";
import { RadioButton } from "primereact/radiobutton";
import { InputText } from "primereact/inputtext";
import { InputNumber } from "primereact/inputnumber";
import { ListBox } from "primereact/listbox";
import useToken from "../../hooks/useToken";

export default function NewSong() {
  const { token } = useToken();

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
  const [newParent, setNewParent] = useState(null);

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
        parent: newParent,
      }),
    };
    fetch(`http://localhost:8081/api/songcollection/songs`, requestOptions);
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
          <InputNumber
            placeholder="Parent"
            onChange={(e) => setNewParent(e.value)}
          />
          <br />
          <br />
          <Button label="Create" onClick={onCreateClick} />
        </SplitterPanel>
      </Splitter>
    </div>
  );
}
