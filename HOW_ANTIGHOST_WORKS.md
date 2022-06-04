# How AntiGhost works

AntiGhost needs to send a "what's the state of this block" request for every single block (technically, what it sends is 'stop breaking this block' to which the server responds with the current state), which, with a range of 4, already means (9x9x9 =) 729 requests.

Many servers use a plugin (Velocity) to allow different versions of MC to connect; the default behavior of Velocity is to kick players when they send more than 800 requests in a short time. Which means increasing the range will get you kicked on many servers. And the more block requests you send, the more likely is it for a server owner to do something against it, so, again, you lose.
