# Presentation

An Infrastructure as a Service cloud computing platform.

The application is made of 4 microservices + the vm instance itself:

- compute-manager:
  Manages user projects, takes in user's VM configuration and sends it to an orchestrator and a data source.
  Maps VMs to their respective names, descriptions, projects, tags, users, regions, CPU counts, storage sizes and ram sizes.
- compute-orchestrator:
  Manages compute nodes. Takes in VM specs (only CPU count, RAM, storage size and region), a UUID and a token from vm-manager, finds a suitable compute node, and uses gRPC to create and spin up the VM instance. It also uses gRPC to issue VM shutdown/start/restart requests.
- cloud-data-source:
  Stores user's instance configuration.
  Receives a UUID and a hashed input from the compute-manager, and stores it. Sends back a token.
- compute-node:
  Installed on machines with spare cpu, memory and storage resources.
  Registers to the compute-orchestrator, and sends a periodic heartbeat.
  Takes in VM specs (CPU counts, RAM, storage size) a UUID and a token.
  Creates an XML description of the VM instance which is then handled by libvirt to spin up a new VM instance.
  The XML config contains an encoded url to the cloud-data-source with the instance's predefined UUID and the token passed down by the compute-orchestrator.
- Virtual Machine:
  Once spun up, leverages the "cloud-init" method for fetching the user's configuration.
  Ideally, the image used is a preinstalled distribution with an additional configuration to "cloud-init" where you implement how the mechanism would contact the cloud data source, how to authenticate, how to take in the data and pre-process it (decrypt, parse, etc...).
  For simplicity, I opted in building a "dumb" implementation of this part (no hashing of data, no fancy auth method - auth uses a forever valid token for each instance).

## Architecture
```
                               ┌───────────────┐
                               │ message queue │
                               │    (kafka)    │
                               └───────┬───────┘
                                       │ topic1 ┌───────────────────┐ REST API
                                       │───────►│ cloud-data-source │◄──────────────┐
  ┌────────┐  ┌─────────────────┐ msg1 │        └───────────────────┘               │
  │ client │─►│ compute-manager │─────►│                                            │
  └────────┘  └─────────────────┘ msg2 │ topic2  ┌──────────────────────┐           │
                                       │────────►│ compute-orchestrator │           │
                                       │         └───────────┬──────────┘           │
                                                             │                      │
                                                             │ gRPC                 │
                    db                                       │                      │
              ┌────────────┐                                 ▼                      ▼
              │ postgreSQL │                           ┌──────────────┐    ┌─────────────────┐
              └─────┬──────┘                           │ compute-node │───►│ virtual machine │
                    │──────► compute-manager           └──────────────┘    └─────────────────┘
                    │──────► compute-orchestrator

              ┌────────────┐
              │   CouchDb  │
              └─────┬──────┘
                    │──────► cloud-data-source
```

## Tech stack

Java - Python - TypeScript - Spring Boot - FastAPI - Angular - gRPC - Kafka - KVM - Libvirt - PostgreSQL - CouchDB
