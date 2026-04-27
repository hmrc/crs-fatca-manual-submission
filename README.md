
# crs-fatca-manual-submission

Backend service to support manual file submission from crs & fatca.

The frontend to this service can be found [here](https://github.com/hmrc/crs-fatca-manual-submission-frontend)

---

### Running the service

Service manager: CRS_FATCA_ALL

**Port:** 10040

---

### API

| Task                     | Supported methods | Description                                                   |
|--------------------------|-------------------|---------------------------------------------------------------|
| /read-submission-history | POST              | Reads Submission History for fiid(if provided) & subscription |
                                    |
---

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").