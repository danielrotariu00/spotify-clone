import React, { useState, useEffect } from "react";
import { Paginator } from "primereact/paginator";
import { Card } from "primereact/card";
import { Button } from "primereact/button";
import { Splitter, SplitterPanel } from "primereact/splitter";
import { RadioButton } from "primereact/radiobutton";
import { InputText } from "primereact/inputtext";
import { Link } from "react-router-dom";

export default function Artists() {
  const [artists, setArtists] = useState([]);
  const [first, setFirst] = useState(0);
  const [rows, setRows] = useState(5);
  const [page, setPage] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const [name, setName] = useState(null);
  const exactMatchOptions = [
    { name: "NO", value: false },
    { name: "YES", value: true },
  ];

  const [selectedExactMatch, setSelectedExactMatch] = useState(
    exactMatchOptions[0]
  );

  useEffect(() => {
    getArtists(page, rows);
  }, []);

  const onPageChange = (event) => {
    setFirst(event.first);
    setRows(event.rows);
    setPage(event.page);

    getArtists(event.page, event.rows);
  };

  const onFilterClick = () => {
    getArtists(page, rows);
  };

  const getArtists = (page, rows) => {
    let url = `http://localhost:8081/api/songcollection/artists?page=${page}&maxItems=${rows}`;

    if (name !== null) {
      url += `&name=${name}&exactMatch=${selectedExactMatch.value}`;
    }

    fetch(url)
      .then((response) => response.json())
      .then((data) => {
        setArtists(data.artists);
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
          <br />
          <br />
          <Button label="Filter artists" onClick={onFilterClick} />
        </SplitterPanel>
        <SplitterPanel
          style={{ overflowY: "scroll" }}
          className="flex align-items-center justify-content-center"
        >
          <ul>
            {artists.map((artist) => (
              <div>
                <Card
                  title={
                    <Link to={`/artists/${artist.id}`}>{artist.name}</Link>
                  }
                  style={{ height: "5em", width: "20em" }}
                ></Card>
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
