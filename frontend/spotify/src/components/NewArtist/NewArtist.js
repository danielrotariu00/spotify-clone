import React, { useState } from "react";
import { Button } from "primereact/button";
import { Splitter, SplitterPanel } from "primereact/splitter";
import { RadioButton } from "primereact/radiobutton";
import { InputText } from "primereact/inputtext";
import { InputNumber } from "primereact/inputnumber";
import useToken from "../../hooks/useToken";

export default function NewArtist() {
  const { token } = useToken();

  const [newId, setNewId] = useState(null);
  const [newName, setNewName] = useState(null);

  const isActiveList = [
    { name: "TRUE", value: true },
    { name: "FALSE", value: false },
  ];
  const [newSelectedIsActive, setNewSelectedIsActive] = useState(
    isActiveList[0]
  );

  const onCreateClick = () => {
    const requestOptions = {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        name: newName,
        isActive: newSelectedIsActive.value,
      }),
    };
    fetch(
      `http://localhost:8081/api/songcollection/artists/${newId}`,
      requestOptions
    );
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
          <h2>Create New Artist</h2>
          <h3>Id</h3>
          <InputNumber placeholder="Id" onChange={(e) => setNewId(e.value)} />
          <h3>Name</h3>
          <InputText
            placeholder="Name"
            onChange={(e) => setNewName(e.target.value)}
          />

          <h3>Type</h3>
          {isActiveList.map((element) => {
            return (
              <div key={element.name} className="field-radiobutton">
                <RadioButton
                  inputId={element.name}
                  name="element"
                  value={element}
                  onChange={(e) => setNewSelectedIsActive(e.value)}
                  checked={newSelectedIsActive.name === element.name}
                />
                <label htmlFor={element.name}>{element.name}</label>
              </div>
            );
          })}
          <br />
          <br />
          <Button label="Create" onClick={onCreateClick} />
        </SplitterPanel>
      </Splitter>
    </div>
  );
}
