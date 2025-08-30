import api from "./api";
import { useEffect, useState } from "react";

function NotesList() {
  const [notes, setNotes] = useState([]);

  useEffect(() => {
    api.get("/notes")
        .then(res => setNotes(res.data))
        .catch(err => console.error(err));
  }, []);

  return (
      <div>
        <h2>My Notes</h2>
        <ul>
          {notes.map(note => (
              <li key={note.id}>{note.title}</li>
          ))}
        </ul>
      </div>
  );
}

export default NotesList;
