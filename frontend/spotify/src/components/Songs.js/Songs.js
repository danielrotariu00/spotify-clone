import React, { useState, useEffect } from "react";
import { Paginator } from "primereact/paginator";
import { Card } from "primereact/card";
import { Button } from "primereact/button";
import { Splitter, SplitterPanel } from "primereact/splitter";
import { RadioButton } from "primereact/radiobutton";
import { InputText } from "primereact/inputtext";
import { InputNumber } from "primereact/inputnumber";
import { Link } from "react-router-dom";

export default function Songs() {
  const [songs, setSongs] = useState([]);
  const [first, setFirst] = useState(0);
  const [rows, setRows] = useState(5);
  const [page, setPage] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const [name, setName] = useState(null);
  const [year, setYear] = useState(null);
  const exactMatchOptions = [
    { name: "NO", value: false },
    { name: "YES", value: true },
  ];

  const [selectedExactMatch, setSelectedExactMatch] = useState(
    exactMatchOptions[0]
  );
  const genres = [
    { name: "any", value: null },
    { name: "METAL", value: "METAL" },
    { name: "ROCK", value: "ROCK" },
    { name: "RAP", value: "RAP" },
  ];
  const [selectedGenre, setSelectedGenre] = useState(genres[0]);

  useEffect(() => {
    getSongs(page, rows);
  }, []);

  const onPageChange = (event) => {
    setFirst(event.first);
    setRows(event.rows);
    setPage(event.page);

    getSongs(event.page, event.rows);
  };

  const onFilterClick = () => {
    getSongs(page, rows);
  };

  const getSongs = (page, rows) => {
    console.log(year);
    let url = `http://localhost:8081/api/songcollection/songs?page=${page}&maxItems=${rows}`;

    if (name !== null) {
      url += `&name=${name}&exactMatch=${selectedExactMatch.value}`;
    }

    if (selectedGenre.name !== "any") {
      url += `&genre=${selectedGenre.value}`;
    }

    if (year !== null) {
      url += `&year=${year}`;
    }

    fetch(url)
      .then((response) => response.json())
      .then((data) => {
        setSongs(data.songs);
        setTotalElements(data.totalElements);
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
          <h2>Filters</h2>
          <h3>Name</h3>
          <InputText
            placeholder="Name"
            onChange={(e) => setName(e.target.value)}
          />
          <h3>Exact match</h3>
          {exactMatchOptions.map((option) => {
            return (
              <div key={option.name} className="field-radiobutton">
                <RadioButton
                  inputId={option.name}
                  name="option"
                  value={option}
                  onChange={(e) => setSelectedExactMatch(e.value)}
                  checked={selectedExactMatch.name === option.name}
                />
                <label htmlFor={option.name}>{option.name}</label>
              </div>
            );
          })}
          <h3>Genre</h3>
          {genres.map((genre) => {
            return (
              <div key={genre.name} className="field-radiobutton">
                <RadioButton
                  inputId={genre.name}
                  name="genre"
                  value={genre}
                  onChange={(e) => setSelectedGenre(e.value)}
                  checked={selectedGenre.name === genre.name}
                />
                <label htmlFor={genre.name}>{genre.name}</label>
              </div>
            );
          })}
          <h3>Year</h3>
          <InputNumber placeholder="Year" onChange={(e) => setYear(e.value)} />
          <br />
          <br />
          <Button label="Filter songs" onClick={onFilterClick} />
        </SplitterPanel>
        <SplitterPanel
          style={{ overflowY: "scroll" }}
          className="flex align-items-center justify-content-center"
        >
          <ul>
            {songs.map((song) => (
              <div>
                <Card
                  title={<Link to={`/songs/${song.id}`}>{song.name}</Link>}
                  subTitle={song.genre}
                  style={{ height: "10em", width: "20em" }}
                >
                  <p>Year: {song.year}</p>
                </Card>
                <br />
              </div>
            ))}
          </ul>
          <Paginator
            first={first}
            rows={rows}
            totalRecords={totalElements}
            rowsPerPageOptions={[5, 10, 20]}
            onPageChange={onPageChange}
          ></Paginator>
        </SplitterPanel>
      </Splitter>
    </div>
  );
}
