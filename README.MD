# Gaslight

## Detectability Challenge
Mojang recently [tweeted](https://twitter.com/Minecraft/status/1553077938639085568) implying that Gaslight is detectable, while it is (currently) detectable in some cases, it is not detectable in all cases, and if you know what you are doing, generating indistinguishable reports is more than doable. We have included 3 example reports, containing all data sent to Mojang (although we redacted the server ip), at least one of them is legitimate, and at least one is produced via Gaslight. Which example is legitimate, which is from Gaslight? We will attach any correct cryptographic proof to the top of this readme, and probably look like idiots.

To prove we aren't changing the answer, the (salted) sha512 hash is `95c4f235fb3a6265f8b9f0114b0a9fd613d33937aea326e314c502f42d7051ebea88faa79e95895d9538171821ea5cbe97727decc7a1085fc56e7f9b069f5e48`

### Gaslit report

0xAda: do you want to pvp

0xAda: /msg [gaslight user] are you breaking tos

0xAda: *?

Gildfesh: of course!

### Legitimate report

0xAda: /msg [any other user] [any message]

0xAda: are you breaking tos

0xAda: *?

Gildfesh: of course!

## (working as of 1.19.2)
[https://www.youtube.com/watch?v=ipkFCrbB3Cg](https://www.youtube.com/watch?v=ipkFCrbB3Cg)

1.19.1-pre4/5 was a patch to the initial version of Gaslight. Not long before their announcement banning all blockchain technologies from the game, Mojang themselves added a blockchain to the chat system, this clusterfuck of a system had us stumped, not because it fixed Gaslight, but because it doesn't make any sense.

Mojang's fix was to add a chat chain system, this is beyond the scope of a youtube/github description, but you can read in depth about it [here](https://gist.github.com/kennytv/ed783dd244ca0321bbd882c347892874), but the important part is that it stops you adding messages after the fact. You can't remove messages anymore, because the last seen field would show messages that no longer exist, and you can't add messages because it would invalidate the signatures of messages surrounding it as it would not be included in the last seen field.

The workaround Gaslight uses is private messages. In order to keep the chain consistent for every player, Mojang added a new packet that sends only the signature of a message for any message a player cannot see. Mojang, however, forgot the one thing that got them in this mess in the first place, you can't trust the client. With just the chat chain, how do they know if you actually received a message, or if it was a private message that you couldn't see? Every message you can see on your client will appear to Mojang as if it was sent in public chat.

Utilising private messages also gives us the ability to remove messages. With some exceptions, Mojang has no way of knowing if you received the full message, or just the header, therefore we can remove the body of messages, leaving it as just a header and making it identical to an equivalent private message.

By sending a private message to yourself with the desired context, then sending a message in public chat expecting a particular response, you can have context that is only visible to you and Mojang, and you can "remove" the message you sent in public chat, because Mojang has no way of knowing if you received the full message, or just the header. This is slightly harder to use than the previous iteration of Gaslight, but overall, the functionality is the same.

# Original Exploit

[https://www.youtube.com/watch?v=uyqi-CzB8Dg](https://www.youtube.com/watch?v=uyqi-CzB8Dg)

1.19.1-rc1 changed how abuse reports were sent to Mojang, context of other messages is now included. Mojang does not verify that these messages were actually sent to the server, or even that the players were on the server at the time. You can modify the request sent to Mojang to fake context around the message, messages can be signed as if they were sent in the past to make innocent messages look malicious, especially if you have keys for multiple accounts.

## Real Conversation

[12:59] player 1: want to pvp?

[13:00] player 2: of course!

## Fake Conversation, Reported to Mojang

[12:59] player 1: are you breaking mojang's terms of service?

[13:00] player 2: of course!
