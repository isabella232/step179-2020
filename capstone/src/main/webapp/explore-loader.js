async function loadExplore () {
  getListings();
  loadLabels();
}

async function loadLabels () {
  const listElement = document.getElementById('labels-select-options');

  const query = '/labels';
  const response = await fetch(query);
  const labels = await response.json();
  for (var label of labels) {
    listElement.innerHTML += '<option value="' + label + '">'; // Forgoing templates because this is simple. 
  }
}

async function addFilterAndReload () {
  const filter = document.getElementById('labels-select').value;
  document.getElementById('labels').innerHTML += '<li>' + filter + '</li>';
  document.getElementById('labels-select').value = '';
  getListings();
}

async function getListings () {
  const sortType = document.getElementById('sort-type').value;
  const labels = getLabelQueryString();

  var query = '/explore?sort=' + sortType + '&labels=' + labels;
  const response = await fetch(query);
  const json = await response.json();
  loadListings(json);
}

async function loadListings (json) {
  document.getElementById('club-listings').innerHTML = ''; // Clear the listings div, for if we're refreshing the listings
  const template = document.querySelector('#club-listing');
  for (var club of json) {
    imageUrl = 'images/logo.png';
    if (club.logo != '') {
      imageUrl = await getImageUrl(club.logo);
    }
    template.content.querySelector('#club-logo').src = imageUrl;
    template.content.querySelector('#club-name').innerHTML = club.name;
    template.content.querySelector('#club-name').href = 'about-us.html?name=' + club.name;
    template.content.querySelector('#description').innerHTML = club.description;
    template.content.querySelector('#members').innerHTML = club.members.length + ' members';
    template.content.querySelector("#club-join-container").innerHTML = getJoinButton(club.name);    
    var clone = document.importNode(template.content, true);
    document.getElementById('club-listings').appendChild(clone);
  }
}

function getLabelQueryString () {
  const element = document.getElementById('labels');
  const labels = element.getElementsByTagName('li');
  var queryString = '';
  for (var label of labels) {
    queryString += label.innerText + ',';
  }
  return queryString.slice(0, -1); // Takes out the last comma. 
}

function getJoinButton(clubName) {
  var joinButton = '<button name="join" value="'
    + clubName
    + '" formmethod="POST">Join Club</button>';
  return joinButton;
}
