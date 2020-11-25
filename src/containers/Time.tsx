import React, { useState, useEffect } from "react";
export default function Time() {
  const [currentTime, setCurrentTime] = useState(0);

  useEffect(() => {
    fetch("/api/time")
      .then((res) => {
        return res.json();
      })
      .then((data) => {
        setCurrentTime(data.time);
      });
  }, []);

  return (
    <div className="App">
      <header className="App-header">
        ... no changes in this part ...
        <p>The current time is {currentTime}.</p>
      </header>
    </div>
  );
}
