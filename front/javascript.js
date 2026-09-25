const best = document.getElementById("quality");

best.addEventListener("change", () =>{
  document.getElementById("quality_value").style.display = best.checked ? "block" : "none";
});

async function start_download() {
  const quality = document.getElementById("quality_value").value;

  let data_json;
  if (best.checked) {
      data_json = {
        wideo: document.getElementById("wideo").checked,
        quality: quality,
        playlist: document.getElementById("playlist").checked,

        url: document.getElementById("url").value,
      };
  }else{
      data_json = {
        wideo: document.getElementById("wideo").checked,
        quality: 0,
        playlist: document.getElementById("playlist").checked,

        url: document.getElementById("url").value,
      };
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
    document.getElementById("input").textContent = data;

    if (!res.ok) {
      console.error("Błąd serwera (status " + res.status + "):", data);
      return null;
    }

    return data;
  } catch (err) {
    console.error("Błąd pobierania:", err);
    document.getElementById("input").textContent = err;
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
    document.getElementById("input").textContent = data;

    if (!res.ok) {
      console.error("Błąd serwera (status " + res.status + "):", data);
      return null;
    }

    return data;
  } catch (err) {
    console.error("Błąd pobierania:", err);
    document.getElementById("input").textContent = err;
    return null;
  }

}

const update = document.getElementById("update");

update.addEventListener("click", () => {
  update_dlp();
});