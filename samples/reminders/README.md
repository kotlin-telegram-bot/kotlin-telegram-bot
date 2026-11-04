# Natural-language reminders

An optional JVM sample using the published [`io.github.alox1d:dateparser-kotlin:0.1.3`](https://central.sonatype.com/artifact/io.github.alox1d/dateparser-kotlin/0.1.3) dependency. DateParser runs offline and is a dependency of this sample only.

## Run

Use Java 17, as in the repository's CI. Create a Telegram bot with [BotFather](https://t.me/BotFather), then set both environment variables before running from the repository root:

```sh
export TELEGRAM_BOT_TOKEN='<your bot token>'
export REMINDER_TIME_ZONE='Europe/Berlin'
./gradlew :reminders:run
```

`REMINDER_TIME_ZONE` must be a valid IANA timezone such as `Europe/Berlin`, `Asia/Tokyo`, or `UTC`. The bot uses that zone for every chat; it does not infer a user's zone from Telegram or message text. Keep the token private. The sample disables Telegram library logging and does not log message contents or delivery errors.

Open a **private chat** with the bot and send:

```text
/remind Call Alice tomorrow at 7pm
/confirm
```

Russian works too:

```text
/remind Позвонить Алисе завтра в 19:00
/confirm
```

The `/remind` response shows the parsed title, full local date/time, UTC offset, and configured timezone. Review it before sending `/confirm`. Nothing is scheduled or delivered before confirmation. For example, with reference `2026-11-01T10:00:00Z` and zone `UTC`, both examples resolve to `2026-11-02 19:00:00 Z [UTC]`.

Use `/cancel` to clear this chat's draft and scheduled reminder. Each chat may hold one draft and one scheduled reminder. A new valid `/remind` replaces the draft; an invalid replacement discards the old draft so `/confirm` cannot accidentally schedule it. If a reminder is already scheduled, another draft may be previewed, but it cannot be confirmed until the scheduled reminder has been delivered or `/cancel` has cleared both. Other chats cannot confirm or cancel your reminder. Group and channel commands are rejected.

## Parsing boundaries

Use one future time and a nonblank title. No match, multiple matches, date ranges, recurring reminders, and past times are rejected. Use AM/PM (`7pm`) or a 24-hour clock (`19:00`) for explicit times; ambiguous English bare hours such as `tomorrow at 7` are rejected. The sample also rejects invalid numeric clock fields and unconsumed numeric clock/date fragments rather than accepting the parser's fallback time as a requested clock.

DateParser 0.1.3 does not flag every unsupported expression. The sample adds narrow checks for common unparsed forms such as `daily at 5pm`, `between 5 and 8`, and a clock followed by a timezone abbreviation or IANA zone, including labels in parentheses or square brackets such as `7pm (UTC)` and `19:00 [Europe/Berlin]`. Write the time in `REMINDER_TIME_ZONE` and omit embedded zones. These checks are conservative, not a complete natural-language grammar; inspect every preview before confirming. A frequency word in an ordinary title, such as `Read daily report tomorrow`, remains allowed.

Date-only phrases such as `/remind Call Alice tomorrow` retain DateParser's reference clock time. At a reference of 10:00 UTC, this previews tomorrow at **10:00**, rather than a default evening time. Always check the displayed time and zone. Confirmation rechecks that the preview's time is still in the future; it does not reparse relative wording later.

## Scheduling and limitations

The service has an explicit clock, timezone, and delayed callback boundary. A separate scheduled executor waits for due times and makes one plain-text Telegram delivery attempt, so waiting for a reminder does not block update handling. Delivery claims and cancellation are synchronized; duplicate confirmation and stale callbacks cannot send a reminder twice. Network delivery runs outside the service lock. Once a delivery has already been claimed, `/cancel` cannot recall the message in flight.

This is an in-memory sample. Restarting or stopping the process loses all drafts and scheduled reminders. It stores at most 100 total drafts and scheduled reminders, limits reminder input to 2000 characters, and removes expired drafts when new drafts are submitted. Cancelled executor tasks are removed from its queue. Shutdown cancels pending work and closes the executor. A slow Telegram request can delay other due reminders because the sample uses one delivery worker. Delays use the system clock when registered; this is not a persistent scheduler with clock-change reconciliation. Delivery failures are not retried, so a reminder may be lost during an outage.

## Tests

No Telegram token or network delivery is needed for unit tests:

```sh
./gradlew :reminders:test :reminders:ktlint
```

Tests use the actual published DateParser dependency, literal expected instants, a controlled clock, and a fake delayed-delivery boundary. They cover English/Russian parsing, invalid and ambiguous text, unsupported ranges/recurrence, late and repeated confirmation, chat isolation, cancellation races, capacity, and executor lifecycle. The repository's full verification command is `./gradlew build`.
