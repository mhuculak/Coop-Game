<!DOCTYPE html>
<html>
<head>
    <title>Cooperation</title>
    <script src="resources/sockjs-0.3.4.js"></script>
    <script src="resources/stomp.js"></script>
    <script type="text/javascript">
        var stompClient = null;
        var currentOptions = null;
        var mapDiv = null;
        var playerStepSize = 10;
        var windowStepSize = 50; 
        var windowSize = { x: 500, y: 500};         
        var mapWidth = null;
        var mapHeight = null;
        var playerPos = { x: windowSize.x/2, y: windowSize.y/2};
        var windowPos = { x: 0, y: 0};
        var destPos = null;
        var requestedDestPos = null;
        var stepsTaken;
        var stepsNeeded;
        var myTimerID; 
        var gameInfo = { edgeSize: 30.0, yLen: 51.9 };       
        
        var angle;
        var mapImg = new Image();
        var playerName = null;
        mapImg.onload = function() {
            mapWidth = mapImg.width;
            mapHeight = mapImg.height;
        }
        mapImg.src = "resources/map.jpg";
        var onMouseDown = function(e) {
            startMove(e);
            
            timerCount = 0;
        }

        var onMouseUp = function() {
//            console.log("Done moving");
            clearInterval(myTimerID);
            myTimerID = 0;
        }

        function moveTimeout() {
//            console.log("Step " + stepsTaken + " angle " + angle);
            var remaining = keepMoving(angle);
            stepsTaken++;
            if (remaining < 1.0) {
                console.log("stop moving because distance to target is " + remaining);
                clearInterval(myTimerID);
                myTimerID = 0;
            }
        }

        function distRemaining() {
            var dx =  destPos.x - playerPos.x;
            var dy = destPos.y - playerPos.y;
            var dist = Math.sqrt(dx*dx + dy*dy);
            angle = Math.atan2(dx, dy);
            return dist;
        }

        function prepareMove(event) {
            mapDiv = document.getElementById('mapDiv');            
            requestedDestPos = new Object();
            console.log("Mouse pos is " + event.clientX + " " + event.clientY);
            console.log("offsets are " + mapDiv.offsetLeft + " " +  mapDiv.offsetTop);
            console.log("window pos is " + windowPos.x + " " + windowPos.y);          
            requestedDestPos.x = event.clientX - windowPos.x - mapDiv.offsetLeft + gameInfo.edgeSize/2;
            requestedDestPos.y = event.clientY - windowPos.y - mapDiv.offsetTop + gameInfo.yLen/2;
            stompClient.send("/game/dest", {}, JSON.stringify({name: playerName, position: requestedDestPos})); 
        }

        function startMove() {                      
            var dist = distRemaining();
            stepsNeeded = Math.round(dist/playerStepSize);
            stepsTaken = 0;
            keepMoving(angle);
            console.log("startMove with player position  " + playerPos.x + " " + playerPos.y);
            console.log("startMove with destination position  " + destPos.x + " " + destPos.y);
            console.log("startMove with angle " + angle);
            myTimerID = setInterval( moveTimeout, 500);
        }

        function keepMoving(angle) {
                     
            var useStep = playerStepSize;
            var dist = distRemaining();
            if (dist < useStep) {
                useStep = dist;
            }
//            console.log("Distance remaining is " + dist + " using step " + useStep);
            var dx = useStep * Math.sin(angle); 
            var dy = useStep * Math.cos(angle);             
            playerPos.x = playerPos.x + dx;
            if (playerPos.x < 0) {
                playerPos.x = 0;
            }
            if (playerPos.x > mapWidth) {
                playerPos.x = mapWidth;
            }
            playerPos.y = playerPos.y + dy;
            if (playerPos.y < 0) {
                playerPos.y = 0;
            }
            if (playerPos.y > mapHeight) {
                playerPos.y = mapHeight;
            }
//            console.log("New position is " + playerPos.x + " " + playerPos.y);
            stompClient.send("/game/move", {}, JSON.stringify({name: playerName, position: playerPos}));
            display();
            return distRemaining();
        } 

        function display() {
            var map = document.getElementById('map');
            var playerIcon = document.getElementById('player');
            if (playerPos.x < windowPos.x) {
                windowPos.x = windowPos.x - windowStepSize;
                if (windowPos.x < 0) {
                    windowPos.x = 0;
                }
            }
            if (playerPos.y < windowPos.y) {
                windowPos.y = windowPos.y - windowStepSize;
                if (windowPos.y < 0) {
                    windowPos.y = 0;
                }
            }
            if (playerPos.x > windowPos.x + windowSize.x - windowStepSize) {                
                windowPos.x = windowPos.x + windowStepSize;
                if (windowPos.x + windowSize.x > mapWidth) {
                    windowPos.x = mapWidth - windowSize.x;
                }
            }
            if (playerPos.y > windowPos.y + windowSize.y - windowStepSize) {
                windowPos.y = windowPos.y + windowStepSize;
                if (windowPos.y + windowSize.x > mapHeight) {
                    windowPos.y = mapWidth - windowSize.y;
                }
            }
            var mapx = -1 * windowPos.x;
            var mapy = -1 * windowPos.y;
            map.style.left = mapx + 'px';
            map.style.top = mapy + 'px';
//            console.log("Adjust positions by " + gameInfo.edgeSize/2 + " " + gameInfo.yLen/2)
            var playerx = playerPos.x - windowPos.x - gameInfo.edgeSize/2;
            var playery = playerPos.y - windowPos.y - gameInfo.yLen/2;
            playerIcon.style.left =  playerx + 'px';
            playerIcon.style.top =  playery + 'px';
        }

        function setConnected(connected) {
            document.getElementById('connect').disabled = connected;
            document.getElementById('disconnect').disabled = !connected;
            document.getElementById('chatDiv').style.visibility = connected ? 'visible' : 'hidden';
            document.getElementById('msgDiv').innerHTML = '';
            document.getElementById('descDiv').innerHTML = '';
            
            var initialOptions = { message: "Press Start to start the game", actions: [{action: 'Start', type: null, objects: null}]};                       
            showOptions(initialOptions);
            currentOptions = initialOptions;
            playerPos = { x: windowSize.x/2, y: windowSize.y/2};
            windowPos = { x: 0, y: 0};           
            display();
        }

        function start() { 
            mapDiv = document.getElementById('mapDiv');           
//            mapDiv.addEventListener('mousedown', startMove , false);
            mapDiv.onclick = function(e) {
                prepareMove(e);
            }
            document.getElementById('writeDiv').style.visibility='hidden';
            
            var socket = new SockJS('/coop-game/begin');
			stompClient = Stomp.over(socket);
            stompClient.connect({}, function(frame) {
                setConnected(true);
                console.log('Connected: ' + frame);
                stompClient.subscribe('/topic/showMessage', function(response){
                	showMessage(JSON.parse(response.body).msg);
                });
                
                stompClient.subscribe('/topic/showOptions', function(response){
                    console.log('Received: ' + response.body);
                    currentOptions = JSON.parse(response.body);                                     
                    showOptions(currentOptions);
                    if (currentOptions.position != null) {
                        playerPos = currentOptions.position;                        
                        display();
                    }
                });

                stompClient.subscribe('/topic/dest', function(response){
                    console.log('Received: ' + response.body);
                    destPos = JSON.parse(response.body);
                    console.log('received dest: ' + destPos);
                    if (destPos == null) {
                        destPos = requestedDestPos;
                        console.log('using requested dest: ' + destPos);
                    }
                    startMove();                                                                            
                });

                stompClient.subscribe('/topic/gameInfo', function(response){
                    console.log('Received: ' + response.body);
                    gameInfo = JSON.parse(response.body);
                    gameInfo.yLen = gameInfo.edgeSize * Math.sqrt(3.0);                                                        
                });
            });

        }
               
        function quit() {
            stompClient.send("/game/quit", {}, {});
            stompClient.disconnect();
            setConnected(false);
            console.log("Disconnected");
        }
        function sendMessage() {
            var msgText = document.getElementById('msgText').value;            
            stompClient.send("/game/chat", {}, JSON.stringify({ 'msg': msgText }));
        }
        function showMessage(message) {
            console.log('showMessage: ' + message);
            var response = document.getElementById('msgDiv');
            var p = document.createElement('p');
            p.style.wordWrap = 'break-word';
            p.appendChild(document.createTextNode(message));
            response.appendChild(p);
        }
        function showDescription(desc) {
            document.getElementById('descDiv').innerHTML = '';
            console.log('Show description: ' + desc);
            var response = document.getElementById('descDiv');
            var p = document.createElement('p'); 
            p.style.wordWrap = 'break-word';
            p.appendChild(document.createTextNode(desc));
            response.appendChild(p);
        }

        function showOptions(options) {
          showDescription(options.message);                              
          var ctx = document.getElementById('actionDiv');
          ctx.innerHTML = '';
          var actions = options.actions;
          for (var a in actions) {
              var action = actions[a].action;
              var objects = actions[a].objects;
                                       
              var b = document.createElement("input");
              b.type = "button";
              
              b.value = action;             
              b.name = action;
              for (var o in objects) {
                    var obj = objects[o];
                    console.log('Found object: ' + obj);
                    b.name = b.name + ":" + obj.name;             
                    b.value = b.value + " " + obj.name;
              }
              if (action.localeCompare("read")==0) {
                b.value = "read note";                
              }
              if (action.localeCompare("write")==0) {
                document.getElementById('writeDiv').style.visibility='visible';
              }
              
              console.log('Action: ' + action + " button value " + b.value);
               
              b.onclick = function() {                
                    var v = this.name.split(":");
                    var selectedAction = v[0];
                    console.log('selected action ' + selectedAction);
                    v.splice(0,1); // drops 1st element
                    var selObjNames = v;
                    if (selObjNames != null) {
                        console.log('selected ' + selObjNames.length + ' objects');
                        if (selObjNames.length == 0) {
                            selObjNames = null;
                        }
                    }
                    var selObjs = null;
                    var selAction = null;
                    if (currentOptions != null) {
                        var actions = currentOptions.actions;                    
                        for ( var a in actions) {
                            var act = actions[a].action;
                            var objs = actions[a].objects;
                            var type = actions[a].type;

                            if (objs != null) {
                                console.log('matching option with ' + objs.length + ' objects');
                            }
                            
                            if (act.localeCompare(selectedAction)==0) {
                                console.log('selected action matches option ' + act);
                                if (objs == null && selObjNames == null) {
                                    console.log('Found match without objects');
                                    selAction = actions[a];
                                }
                                else {
                                    var objMatches = 0;
                                    for (var i=0 ; i<objs.length ; i++ ) {                                
                                        var oname = objs[i].name;                                
                                        if (oname.localeCompare(selObjNames[i])==0) {
                                            objMatches++;
                                            
                                        }
                                    }
                                    if (objMatches == selObjNames.length) {
                                        console.log('Found match with ' + objMatches + ' objects');
                                        selObjs = objs[i];
                                        selAction = actions[a];
                                        console.log("matched object is ")
                                    }
                                }
                            }
                            else {
                                console.log('selected action ' + selectedAction + ' does not match option ' + act);
                            }
                        }
                    }
                    else {
                        console.log('no current options to match');
                    }
//                    if (selObjs != null) {                        
//                        if (selectedAction.localeCompare("Play as")==0) {
//                            console.log("playing as player " + selObjs[0].name);
//                            playerName = selObjs[0].name;                                                     
//                        }
//                  }                    
                    if (selAction.action.localeCompare("Play As")==0) {
                        playerName = selAction.objects[0].name;
                        console.log("playing as player " + playerName);
                    }
                    if (selAction.action.localeCompare("write")==0) {
                        selAction.objects[1].name = document.getElementById('writeText').value;
                        document.getElementById('writeDiv').style.visibility='hidden';
                    }
                    if (selAction.action.localeCompare("read")==0) {
                        console.log('displaying note' + this.name);
                        showMessage(selAction.objects[1].name);
                    }
                    else {
                        stompClient.send("/game/action", {}, JSON.stringify({ name: playerName, action: selAction }));
                    }                    
              };
                          
                           
              ctx.appendChild(b);
             
          }
        }
    </script>
</head>
<body>
<noscript><h2>Enable Java script and reload this page to run Websocket Demo</h2></noscript>
<h1>Cooperation</h1>
<div>
    <div>
        <button id="connect" onclick="start();">New Game</button>
        <button id="disconnect" disabled="disabled" onclick="quit();">Quit Game</button><br/><br/>
    </div>
        
    <div id="mapDiv" style="position: relative; overflow: hidden; width: 500px; height: 500px;">
        <img id="map" src="resources/map.jpg" style="position: absolute; top: 0; left: 0;"/>
        <img id="player" src="resources/player.png" style="position: absolute; top: 0; left: 0;"/>
    </div>
    <div>
        <p id="descDiv"></p>
    </div>
    <div id="actionDiv">
        
    </div>
    <div id="chatDiv">
        <label>Chat Message:</label><input type="text" id="msgText" />       
        <button id="send" onclick="sendMessage();">Send</button>
        
    </div>    
    <div id="writeDiv">
        <label>Write Message:</label><input type="text" id="writeText" />
    </div>
    <div>
        <p id="msgDiv"></p>
    </div>
</div>
</body>
</html> 