const best = document.getElementById("quality");

best.addEventListener("change", () =>{
  document.getElementById("quality_value").style.display = best.checked ? "block" : "none";
});

const best_sound = document.getElementById("quality_sound");

best_sound.addEventListener("change", () => {
  document.getElementById("quality_value_sound").style.display = best_sound.checked ? "block" : "none";
  document.getElementById("quality_label_sound").style.display = best_sound.checked ? "block" : "none";
});

async function start_download() {
  const quality = document.getElementById("quality_value").value;
  const quality_sound = document.getElementById(("quality_value_sound")).value;

  let data_json = {
    wideo: document.getElementById("wideo").checked,
    quality: null,
    quality_sound: null,
    playlist: document.getElementById("playlist").checked,

    url: document.getElementById("url").value,
  };
  if (best.checked) {
    data_json.quality = quality;
  }else{
    data_json.quality = 0;
  }

  if (best_sound.checked) {
    data_json.quality_sound = quality_sound;
  }else{
    data_json.quality_sound = 0;
  }

  try {
    const res = await fetch(`http://localhost:8080/api/download`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data_json),
    });

    const data = await res.text();
    document.getElementById("output").textContent = data;

    if (!res.ok) {
      console.error("Błąd serwera (status " + res.status + "):", data);
      return null;
    }

    return data;
  } catch (err) {
    console.error("Błąd pobierania:", err);
    document.getElementById("output").textContent = err;
    return null;
  }

}

const download = document.getElementById("download");

download.addEventListener("click", () => {
  start_download();
});


async function update_dlp() {
  try {
    const res = await fetch(`http://localhost:8080/api/update`, {
      method: "POST",
    });

    const data = await res.text();
    document.getElementById("output").textContent = data;

    if (!res.ok) {
      console.error("Błąd serwera (status " + res.status + "):", data);
      return null;
    }

    return data;
  } catch (err) {
    console.error("Błąd pobierania:", err);
    document.getElementById("output").textContent = err;
    return null;
  }

}

const update = document.getElementById("update");

update.addEventListener("click", () => {
  update_dlp();
});

