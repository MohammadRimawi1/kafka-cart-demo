# Kafka Cart Demo

A small multi-service project I built to actually get comfortable with Kafka and Spring's `@Scheduled` cron jobs, instead of just reading about them. It simulates a tiny e-commerce flow: someone adds stuff to a cart, maybe checks out, or maybe just wanders off and leaves it sitting there.

There's no database, no frontend, no auth. That's on purpose — the point isn't to build a real store, it's to feel how event-driven services actually talk to each other through Kafka instead of calling each other directly.

## The idea

Three Spring Boot services, one Kafka broker, three topics.

- **cart-service** — exposes two REST endpoints (add item, checkout). Every time either one fires, it publishes an event to Kafka. It also runs a background job every minute that checks all the carts sitting in memory and flags any that have gone untouched for a couple of minutes as abandoned, firing a third event on its own, with nothing external triggering it.
- **notification-service** — does nothing but listen for abandoned-cart events and log a fake reminder. It has no idea cart-service exists beyond the topic name.
- **analytics-service** — listens to all three topics in its own consumer group, keeps running counts of each event type, and exposes a GET endpoint so you can check totals in a browser.

Nothing here calls anything else's REST API. cart-service publishes and walks away — it doesn't know or care who's listening. That's really the whole lesson: producers and consumers are completely decoupled, and you can add or remove listeners without ever touching the service that's producing the events.

## Why three topics instead of one

Each topic represents one specific kind of event, so consumers only subscribe to what they actually care about. notification-service only wants to know about abandoned carts — it has zero interest in checkouts. analytics-service wants everything. Splitting the topics is what makes that possible without either service having to filter out noise it doesn't need.

## Project layout

```
kafka-cart-demo/
├── docker-compose.yml      # spins up Kafka (KRaft mode, no Zookeeper)
├── cart-service/
├── notification-service/
└── analytics-service/
```

Each service is its own independent Spring Boot project with its own `pom.xml`. The root just holds Kafka infrastructure and the shared `.gitignore`.

## Running it

**1. Start Kafka**

```bash
docker compose up -d
docker compose ps
```

Make sure the container actually comes up healthy before moving on.

**2. Run the services**

Start them in this order — cart-service first, since it's the only producer and the other two won't have anything to react to until it's up:

```
cart-service          -> localhost:8081
notification-service  -> no HTTP, listens only
analytics-service     -> localhost:8082
```

**3. Try it out**

Add an item to a new cart:
```bash
curl -X POST "http://localhost:8081/api/carts/items" \
  -H "Content-Type: application/json" \
  -d '{"productId": 101, "quantity": 2}'
```

Grab the `cartId` from the response, then checkout:
```bash
curl -X POST "http://localhost:8081/api/carts/<cartId>/checkout"
```

Or just add an item and leave it alone — after a couple of minutes the abandonment job will pick it up on its own, notification-service will log a reminder, and analytics-service's counter will tick up.

Check the running totals anytime:
```
GET http://localhost:8082/api/analytics/totals
```

**4. Watching the raw events**

Before either consumer service existed, this is how I verified cart-service was actually publishing correctly — just watching the raw topic:

```bash
docker exec -it kafka /opt/kafka/bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 --topic cart-abandoned --from-beginning
```

Swap the topic name for `cart-created` or `cart-checked-out` to watch those instead.

## A few implementation notes

- Carts live in memory (a `ConcurrentHashMap`), nothing is persisted. Restarting cart-service wipes all carts.
- Each service defines its own copy of the event DTOs rather than sharing a library between them. That's deliberate — services shouldn't need to share Java classes across a network boundary just to agree on a message shape. They only need to agree on the JSON.
- notification-service and analytics-service both use manual Kafka acknowledgment instead of auto-commit, so a message only counts as "processed" once the listener actually finishes handling it.
- The abandonment threshold is currently 2 minutes, set in `CartAbandonmentScheduler`. Lower it if you want to test faster without waiting around.

## What this actually taught me

Mainly that decoupling isn't just a buzzword — I built and fully tested cart-service, including the abandonment logic, before notification-service or analytics-service existed at all. Once I did build them, cart-service never needed to change. It was already done. Watching two separate consumer groups read the exact same event log at completely different paces, with neither one aware the other exists, is the part that actually made the concept click.
