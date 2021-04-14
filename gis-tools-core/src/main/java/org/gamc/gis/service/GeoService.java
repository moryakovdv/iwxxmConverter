/**
 * Copyright (C) 2018 Dmitry Moryakov, Main aeronautical meteorological center, Moscow, Russia
 * moryakovdv[at]gmail[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gamc.gis.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.gamc.gis.model.GTCalculatedRegion;
import org.gamc.gis.model.GTCoordPoint;
import org.gamc.gis.model.GTDirectionFromLine;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.LngLatAlt;
import org.geojson.Point;
import org.geojson.Polygon;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.shapefile.shp.JTSUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.util.factory.Hints;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.util.GeometricShapeFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;




public final class GeoService {

	private Logger logger = LoggerFactory.getLogger(GeoService.class);

	private boolean useExternalStore;

	private String storePath;

	private FileDataStore firStoreData = null;
	private SimpleFeatureSource firFeatureSource = null;

	private FileDataStore airportStoreData = null;
	private SimpleFeatureSource airportFeatureSource = null;

	private MathTransform coordTransform = null;
	private MathTransform coordTransformBack = null;
	private boolean latFirst = false;

	private int scaleCoord = 3;

	private CRSAuthorityFactory crsFactory = ReferencingFactoryFinder.getCRSAuthorityFactory("EPSG",
			new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));

	private boolean serviceInit = false;
	private GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
	private Coordinate[] frameMax = { new Coordinate(-180, 85.0), new Coordinate(180, 85.0), new Coordinate(180, -85.0),
			new Coordinate(-180, -85.0), new Coordinate(-180, 85.0) };

	public boolean isUseExternalStore() {
		return useExternalStore;
	}

	public void setUseExternalStore(boolean useExternalStore) {
		this.useExternalStore = useExternalStore;
	}

	public String getStorePath() {
		return storePath;
	}

	public void setStorePath(String storePath) {
		this.storePath = storePath;
	}

	/**
	 * Initializes service with given shape catalog path
	 * 
	 * @param useExternalStore If true uses path to shape catalog passed in @param
	 *                         storePath. Otherwise uses pre-builder resourses
	 * @param storePath        path to external catalog with .shp
	 * @param latFirst         if true, places lattitude first in output coordinate
	 *                         array
	 */
	public void init(boolean useExternalStore, String storePath, boolean latFirst) throws URISyntaxException {

		this.useExternalStore = useExternalStore;
		this.storePath = storePath;
		this.latFirst = latFirst;

		try {

			// map geostore to internal resource or external folder
			if (useExternalStore) {
				firStoreData = FileDataStoreFinder.getDataStore(new File(storePath + "fir/firs_shape.shp"));
				airportStoreData = FileDataStoreFinder.getDataStore(new File(storePath + "airports/airports.shp"));

			} else {
				URL ur = getClass().getResource("/shapes/fir/firs_shape.shp");
				File f = new File(ur.toURI());
				firStoreData = FileDataStoreFinder.getDataStore(f);

				URL urAriport = getClass().getResource("/shapes/airports/airports.shp");
				File fAirport = new File(urAriport.toURI());
				airportStoreData = FileDataStoreFinder.getDataStore(fAirport);
			}

			firFeatureSource = firStoreData.getFeatureSource();
			airportFeatureSource = airportStoreData.getFeatureSource();

			coordTransform = CRS.findMathTransform(firFeatureSource.getSchema().getCoordinateReferenceSystem(),
					crsFactory.createCoordinateReferenceSystem("EPSG:3857"));
			coordTransformBack = CRS.findMathTransform(crsFactory.createCoordinateReferenceSystem("EPSG:3857"),
					crsFactory.createCoordinateReferenceSystem("EPSG:4326"));

			/*
			 * coordTransform =
			 * CRS.findMathTransform(featureSource.getSchema().getCoordinateReferenceSystem(
			 * ), CRS.decode("EPSG:3857")); coordTransformBack =
			 * CRS.findMathTransform(CRS.decode("EPSG:3857"), CRS.decode("EPSG:4326"));
			 */
			if (airportFeatureSource != null && airportStoreData != null && firStoreData != null
					&& firFeatureSource != null && coordTransform != null && coordTransformBack != null) {
				logger.info("FIR data store loaded");
				serviceInit = true;
			}
		} catch (IOException | FactoryException e) {
			logger.error("error geoservice init: ", e);
		}
	}

	public boolean isServiceInit() {
		return serviceInit;
	}

	private SimpleFeatureCollection getAirportFeature(String icao) {
		try {
			SimpleFeatureCollection features = airportFeatureSource
					.getFeatures(CQL.toFilter(String.format("ident='%s'", icao)));
			return features;
		} catch (CQLException | IOException e) {
			logger.error("getAirportFeatures(): ", e);
		}
		return null;
	}

	private SimpleFeatureCollection getAllAirports() {
		try {
			SimpleFeatureCollection features = airportFeatureSource
					.getFeatures(CQL.toFilter(String.format("type='small_airport'")));
			return features;
		} catch (CQLException | IOException e) {
			logger.error("getAirportFeatures(): ", e);
		}
		return null;
	}

	public String jsonForAirport(String icao) throws JsonProcessingException {

		LngLatAlt coordinates = getAirportCoordinates(icao);
		Feature f = new Feature();
		Point point = new Point(coordinates);
		f.setGeometry(point);
		f.setId(icao);
		f.setProperty("icao", icao);

		String json = new ObjectMapper().writeValueAsString(f);
		return json;
	}

	public String jsonForAllAirports() throws JsonProcessingException {
		TreeMap<String, LngLatAlt> mapAirports = mapAirportCoordinates(getAllAirports());
		FeatureCollection resultCollection = new FeatureCollection();

		for (Entry<String, LngLatAlt> mapEntry : mapAirports.entrySet()) {
			LngLatAlt coordinates = mapEntry.getValue();
			String id = mapEntry.getKey();
			Feature f = new Feature();
			Point point = new Point(coordinates);
			f.setGeometry(point);
			f.setId(id);
			f.setProperty("icao", id);
			resultCollection.add(f);
		}

		String json = new ObjectMapper().writeValueAsString(resultCollection);
		return json;
	}

	private TreeMap<String, LngLatAlt> mapAirportCoordinates(SimpleFeatureCollection fCollection) {
		SimpleFeatureIterator iterator = fCollection.features();
		TreeMap<String, LngLatAlt> result = new TreeMap<String, LngLatAlt>();
		try {
			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				Geometry maingeometry = (Geometry) feature.getDefaultGeometry();
				for (int j = 0; j < maingeometry.getNumGeometries(); j++) {
					Geometry geometry = maingeometry.getGeometryN(j);

					if (geometry instanceof org.locationtech.jts.geom.Point) {
						org.locationtech.jts.geom.Point cPoint = (org.locationtech.jts.geom.Point) geometry;
						Coordinate c = cPoint.getCoordinate();
						BigDecimal x = new BigDecimal(c.x).setScale(scaleCoord, RoundingMode.HALF_UP);
						BigDecimal y = new BigDecimal(c.y).setScale(scaleCoord, RoundingMode.HALF_UP);
						LngLatAlt coords = (new LngLatAlt(x.doubleValue(), y.doubleValue()));

						result.put(feature.getAttribute("ident").toString(), coords);

					}

				}
			}
		} catch (Exception e) {
			logger.error("Mapping airports error", e);
		} finally {
			iterator.close();
		}
		return result;

	}

	/** get coordinates for airport by ICAO */
	public LngLatAlt getAirportCoordinates(String icao) {
		SimpleFeatureCollection features = getAirportFeature(icao);
		LngLatAlt coords = mapAirportCoordinates(features).firstEntry().getValue();
		return coords;

	}

	private SimpleFeatureCollection getFirFeatures(String icao) {
		try {
			SimpleFeatureCollection features = firFeatureSource
					.getFeatures(CQL.toFilter(String.format("properti_1='%s' or properti_5='%s'", icao, icao)));
			return features;
		} catch (IOException | CQLException e) {
			logger.error("getFirFeatures(): ", e);
		}
		return null;
	}

	private Coordinate transformCoordinate(Coordinate c) {
		try {
			org.locationtech.jts.geom.Point p = (org.locationtech.jts.geom.Point) JTS
					.transform((Geometry) geometryFactory.createPoint(c), coordTransform);
			return new Coordinate(p.getX(), p.getY());
		} catch (MismatchedDimensionException | TransformException e) {
			logger.error("transformCoordinate(): ", e);
			return c;
		}

	}

	private Coordinate transformCoordinateBack(Coordinate c) {
		try {
			org.locationtech.jts.geom.Point p = (org.locationtech.jts.geom.Point) JTS
					.transform((Geometry) geometryFactory.createPoint(c), coordTransformBack);
			return new Coordinate(p.getX(), p.getY());
		} catch (MismatchedDimensionException | TransformException e) {
			logger.error("transformCoordinate(): ", e);
			return c;
		}

	}

	private ArrayList<Shape> convertModelLines(LinkedList<GTDirectionFromLine> lines) {
		ArrayList<Shape> shapes = new ArrayList<>();
		for (GTDirectionFromLine l : lines) {
			Shape s = new Shape();

			GeomToken t = new GeomToken();
			t.setLine(!l.getLine().isSingleLine());
			t.setDirection(l.getDirection());
			ArrayList<Coordinate> c = new ArrayList<>();

			if (l.getLine().isSingleLine()) {
				if (l.getLine().getSinglePointCoordinate().getAzimuth().equals("W")
						|| l.getLine().getSinglePointCoordinate().getAzimuth().equals("E")) {
					Coordinate cr = transformCoordinate(new Coordinate(l.getLine().getSinglePointCoordinate().getDeg()
							+ l.getLine().getSinglePointCoordinate().getMin() / 60.0, 0));
					c.add(new Coordinate(cr.x, Double.NaN));
				} else {
					Coordinate cr = transformCoordinate(
							new Coordinate(0, l.getLine().getSinglePointCoordinate().getDeg()
									+ l.getLine().getSinglePointCoordinate().getMin() / 60.0));
					c.add(new Coordinate(Double.NaN, cr.y));
				}
			} else {
				l.getLine().getCoordinatesList().forEach(e -> {
					Coordinate cr = new Coordinate(
							(e.getLongitude().getAzimuth().equals("E") ? 1 : -1)
									* (e.getLongitude().getDeg() + e.getLongitude().getMin() / 60.0),
							(e.getLatitude().getAzimuth().equals("N") ? 1 : -1)
									* (e.getLatitude().getDeg() + e.getLatitude().getMin() / 60.0));
					c.add(transformCoordinate(cr));
				});
			}

			s.setCoordinates(c);
			s.setGeometry(t);
			shapes.add(s);
		}
		return shapes;
	}

	private ArrayList<Shape> convertModelPoints(LinkedList<GTCoordPoint> points) {
		ArrayList<Shape> shapes = new ArrayList<>();
		Shape s = new Shape();
		GeomToken t = new GeomToken();
		t.setLine(true);
		t.setDirection("WI");
		for (GTCoordPoint l : points) {
			Coordinate cr = new Coordinate(
					(l.getLongitude().getAzimuth().equals("E") ? 1 : -1)
							* (l.getLongitude().getDeg() + l.getLongitude().getMin() / 60.0),
					(l.getLatitude().getAzimuth().equals("N") ? 1 : -1)
							* (l.getLatitude().getDeg() + l.getLatitude().getMin() / 60.0));
			s.getCoordinates().add(transformCoordinate(cr));
		}
		s.setGeometry(t);
		shapes.add(s);
		return shapes;
	}

	private int isIntersects(Geometry intersect, Geometry geometry) {
		try {
			return intersect.intersects(geometry) ? 1 : 0;
		} catch (Exception e) {
			return -1;
		}
	}

	private FeatureCollection renderFir(SimpleFeatureCollection firFeatures, ArrayList<Shape> shapes) {
		SimpleFeatureIterator iterator = firFeatures.features();
		FeatureCollection resultFeatures = new FeatureCollection();
		boolean found = false;
		try {
			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				Geometry maingeometry = (Geometry) feature.getDefaultGeometry();
				for (int j = 0; j < maingeometry.getNumGeometries(); j++) {
					Geometry geometry = maingeometry.getGeometryN(j);
					try {
						geometry = geometryFactory.createPolygon(frameMax).intersection(geometry);
					} catch (Exception e) {
					}
					geometry = JTS.transform(geometry, coordTransform);
					Envelope envelope = geometry.getEnvelopeInternal();
					Geometry intersect = null;
					for (Shape shape : shapes) {
						if (intersect == null) {
							try {
								intersect = geometryFactory.createPolygon(createShape(shape, envelope));
							} catch (Exception e) {
								intersect = geometryFactory.createLineString(createShape(shape, envelope));
							}
							int intr = isIntersects(intersect, geometry);
							if (intr == 1) {
								try {
									intersect = intersect.intersection(geometry);
								} catch (Exception e) {
									try {
										intersect = intersect.convexHull().intersection(geometry);
									} catch (Exception e1) {
										intersect = intersect.convexHull().intersection(geometry.convexHull());
									}
								}
							} else {
								if (intr == 0) {
									intersect = null;
								}
							}
						} else {
							Geometry shapeg = geometryFactory.createPolygon(createShape(shape, envelope));
							if (isIntersects(intersect, shapeg) == 1) {
								try {
									intersect = intersect.intersection(shapeg);
								} catch (Exception e) {
									intersect = intersect.intersection(shapeg.convexHull());
								}
							} else {
								if (isIntersects(shapeg, geometry) == 1) {
									shapeg = shapeg.intersection(geometry);
									resultFeatures.add(createFeature(shapeg));
								}
							}
						}
					}
					if (intersect != null && intersect.getCoordinates().length > 1) {
						found = true;
						for (int k = 0; k < intersect.getNumGeometries(); k++) {
							Geometry intersecti = intersect.getGeometryN(k);
							resultFeatures.add(createFeature(intersecti));
						}
					}
				}
			}
			iterator.close();
			if (!found) {
				addFir(firFeatures, resultFeatures);
			}
		} catch (Exception e) {
			addFir(firFeatures, resultFeatures);
			logger.error("renderFir(): " + e);
		} finally {
			if (iterator != null) {
				iterator.close();
			}
		}
		return resultFeatures;
	}

	private void addFir(SimpleFeatureCollection features, FeatureCollection featureCollection) {
		SimpleFeatureIterator iterator = null;
		try {
			iterator = features.features();
			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				Geometry maingeometry = (Geometry) feature.getDefaultGeometry();
				for (int j = 0; j < maingeometry.getNumGeometries(); j++) {
					Geometry geometry = maingeometry.getGeometryN(j);
					try {
						geometry = geometryFactory.createPolygon(frameMax).intersection(geometry);
						if (geometry instanceof org.locationtech.jts.geom.Polygon) {
							geometry = JTSUtilities
									.makeGoodShapePolygon((org.locationtech.jts.geom.Polygon) geometry);
						}
					} catch (Exception e) {
					}
					geometry = JTS.transform((Geometry) geometry, coordTransform);
					ArrayList<LngLatAlt> coords = new ArrayList<>();
					for (int i = 0; i < geometry.getCoordinates().length; i++) {
						Coordinate c = geometry.getCoordinates()[i];
						coords.add(new LngLatAlt(c.x, c.y));
					}
					Polygon area = new Polygon(coords);
					Feature f = new Feature();
					f.setGeometry(area);
					featureCollection.add(f);
				}
			}
		} catch (Exception e) {
			logger.error("addFir(): ", e);
		} finally {
			if (iterator != null) {
				iterator.close();
			}
		}
	}

	private Feature createFeature(Geometry intersect)
			throws NoSuchAuthorityCodeException, FactoryException, MismatchedDimensionException, TransformException {
		if (intersect instanceof org.locationtech.jts.geom.Polygon) {
			intersect = JTSUtilities.makeGoodShapePolygon(( org.locationtech.jts.geom.Polygon) intersect);
		}
		ArrayList<LngLatAlt> coords = new ArrayList<>();
		for (int i = 0; i < intersect.getCoordinates().length; i++) {
			Coordinate c = intersect.getCoordinates()[i];
			coords.add(new LngLatAlt(c.x, c.y));
		}
		Polygon area = null;
		Point point = null;
		if (!(intersect instanceof  org.locationtech.jts.geom.Point)) {
			area = new Polygon(coords);
		} else {
			point = new Point(coords.get(0));
		}
		Feature f = new Feature();
		if (area != null) {
			f.setGeometry(area);
		}
		if (point != null) {
			f.setGeometry(point);
		}
		return f;
	}

	private Coordinate[] createShape(Shape shape0, Envelope frame) {
		Shape shape = new Shape(shape0);
		ArrayList<Coordinate> coord = new ArrayList<>(shape.getCoordinates());
		if (shape.getCoordinates().size() == 0) {
			return new Coordinate[] {};
		}
		if (!shape.getGeometry().isLine()) {
			coord = new ArrayList<>();
			if (Double.isNaN(shape.getCoordinates().get(0).x)) {
				coord.add(new Coordinate(frame.getMinX(), shape.getCoordinates().get(0).y));
				coord.add(new Coordinate(frame.getMaxX(), shape.getCoordinates().get(0).y));
				if (shape.getGeometry().getDirection().equals("N")) {
					coord.add(new Coordinate(frame.getMaxX(), frame.getMaxY()));
					coord.add(new Coordinate(frame.getMinX(), frame.getMaxY()));
				}
				if (shape.getGeometry().getDirection().equals("S")) {
					coord.add(new Coordinate(frame.getMaxX(), frame.getMinY()));
					coord.add(new Coordinate(frame.getMinX(), frame.getMinY()));
				}
				if (shape.getGeometry().getDirection().equals("BTN") && shape.getCoordinates().size() == 2) {
					coord = new ArrayList<>();
					coord.add(new Coordinate(frame.getMinX(), shape.getCoordinates().get(0).y));
					coord.add(new Coordinate(frame.getMaxX(), shape.getCoordinates().get(0).y));
					coord.add(new Coordinate(frame.getMaxX(), shape.getCoordinates().get(1).y));
					coord.add(new Coordinate(frame.getMinX(), shape.getCoordinates().get(1).y));
					coord.add(new Coordinate(frame.getMinX(), shape.getCoordinates().get(0).y));
				}
				coord.add(new Coordinate(frame.getMinX(), shape.getCoordinates().get(0).y));
			}
			if (Double.isNaN(shape.getCoordinates().get(0).y)) {
				double x = shape.getCoordinates().get(0).x;
				if (shape.getGeometry().getDirection().equals("W")) {
					coord.add(new Coordinate(x, frame.getMinY()));
					coord.add(new Coordinate(x, frame.getMaxY()));
					coord.add(new Coordinate(frame.getMinX(), frame.getMaxY()));
					coord.add(new Coordinate(frame.getMinX(), frame.getMinY()));
					coord.add(new Coordinate(x, frame.getMinY()));
				}
				if (shape.getGeometry().getDirection().equals("E")) {
					if (frame.getMinX() < 0) {
						x = frame.getMinX();
					}
					coord.add(new Coordinate(x, frame.getMinY()));
					coord.add(new Coordinate(x, frame.getMaxY()));
					coord.add(new Coordinate(frame.getMaxX(), frame.getMaxY()));
					coord.add(new Coordinate(frame.getMaxX(), frame.getMinY()));
					coord.add(new Coordinate(x, frame.getMinY()));
				}
				if (shape.getGeometry().getDirection().equals("BTN") && shape.getCoordinates().size() == 2) {
					coord = new ArrayList<>();
					coord.add(new Coordinate(shape.getCoordinates().get(0).x, frame.getMinY()));
					coord.add(new Coordinate(shape.getCoordinates().get(0).x, frame.getMaxY()));
					coord.add(new Coordinate(shape.getCoordinates().get(1).x, frame.getMaxY()));
					coord.add(new Coordinate(shape.getCoordinates().get(1).x, frame.getMinY()));
					coord.add(new Coordinate(shape.getCoordinates().get(0).x, frame.getMinY()));
				}
			}
		} else {
			if (!shape.getGeometry().getDirection().equals("WI")) {
				for (Coordinate c : shape.getCoordinates()) {
					if (c.x < frame.getMinX() && Math.signum(c.x) == Math.signum(frame.getMinX())) {
						c.x = frame.getMinX();
					}
					if (c.x > frame.getMaxX() && Math.signum(c.x) == Math.signum(frame.getMaxX())) {
						c.x = frame.getMaxX();
					}
					if (c.y < frame.getMinY() && Math.signum(c.y) == Math.signum(frame.getMinY())) {
						c.y = frame.getMinY();
					}
					if (c.y > frame.getMaxY() && Math.signum(c.y) == Math.signum(frame.getMaxY())) {
						c.y = frame.getMaxY();
					}
				}
				coord = new ArrayList<>();
				if (shape.getGeometry().getDirection().equals("N")) {
					if (shape.getCoordinates().size() == 2) {
						Coordinate p1 = createLine(shape.getCoordinates().get(0),
								shape.getCoordinates().get(shape.getCoordinates().size() - 1), frame).get(0);
						Coordinate p2 = createLine(shape.getCoordinates().get(0),
								shape.getCoordinates().get(shape.getCoordinates().size() - 1), frame).get(1);
						coord.add(new Coordinate(p1.x, p1.y));
						coord.add(new Coordinate(p2.x, p2.y));
						if (p1.y > p2.y) {
							coord.add(new Coordinate(p2.x, Math.max(p1.y, frame.getMaxY())));
							coord.add(new Coordinate(p1.x, Math.max(p1.y, frame.getMaxY())));
						} else {
							coord.add(new Coordinate(p2.x, Math.max(p2.y, frame.getMaxY())));
							coord.add(new Coordinate(p1.x, Math.max(p2.y, frame.getMaxY())));
						}
						coord.add(new Coordinate(p1.x, p1.y));
					} else {
						coord.add(new Coordinate(shape.getCoordinates().get(0).x, frame.getMaxY()));
						for (Coordinate c : shape.getCoordinates()) {
							coord.add(new Coordinate(c.x, c.y));
						}
						coord.add(new Coordinate(shape.getCoordinates().get(shape.getCoordinates().size() - 1).x,
								frame.getMaxY()));
						coord.add(new Coordinate(shape.getCoordinates().get(0).x, frame.getMaxY()));
					}
				}
				if (shape.getGeometry().getDirection().equals("S")) {
					if (shape.getCoordinates().size() == 2) {
						Coordinate p1 = createLine(shape.getCoordinates().get(0),
								shape.getCoordinates().get(shape.getCoordinates().size() - 1), frame).get(0);
						Coordinate p2 = createLine(shape.getCoordinates().get(0),
								shape.getCoordinates().get(shape.getCoordinates().size() - 1), frame).get(1);
						coord.add(new Coordinate(p1.x, frame.getMinY()));
						coord.add(new Coordinate(p1.x, p1.y));
						coord.add(new Coordinate(p2.x, p2.y));
						coord.add(new Coordinate(p2.x, frame.getMinY()));
						coord.add(new Coordinate(p1.x, frame.getMinY()));
					} else {
						coord.add(new Coordinate(shape.getCoordinates().get(0).x, frame.getMinY()));
						for (Coordinate c : shape.getCoordinates()) {
							coord.add(new Coordinate(c.x, c.y));
						}
						coord.add(new Coordinate(shape.getCoordinates().get(shape.getCoordinates().size() - 1).x,
								frame.getMinY()));
						coord.add(new Coordinate(shape.getCoordinates().get(0).x, frame.getMinY()));
					}
				}
				if (shape.getGeometry().getDirection().equals("NW")) {
					if (shape.getCoordinates().size() == 2) {
						Coordinate p1 = createLine(shape.getCoordinates().get(0),
								shape.getCoordinates().get(shape.getCoordinates().size() - 1), frame).get(0);
						Coordinate p2 = createLine(shape.getCoordinates().get(0),
								shape.getCoordinates().get(shape.getCoordinates().size() - 1), frame).get(1);
						coord.add(new Coordinate(p1.x, p1.y));
						coord.add(new Coordinate(p2.x, p2.y));
						if (p1.y > p2.y) {
							coord.add(new Coordinate(p2.x, Math.max(p1.y, frame.getMaxY())));
							coord.add(new Coordinate(p1.x, Math.max(p1.y, frame.getMaxY())));
						} else {
							coord.add(new Coordinate(p2.x, Math.max(p2.y, frame.getMaxY())));
							coord.add(new Coordinate(p1.x, Math.max(p2.y, frame.getMaxY())));
						}
						coord.add(new Coordinate(p1.x, p1.y));
					} else {
						coord.add(new Coordinate(frame.getMinX(), frame.getMaxY()));
						if (shape.getCoordinates().get(0).y < shape.getCoordinates()
								.get(shape.getCoordinates().size() - 1).y) {
							coord.add(new Coordinate(frame.getMinX(), shape.getCoordinates().get(0).y));
							for (Coordinate c : shape.getCoordinates()) {
								coord.add(new Coordinate(c.x, c.y));
							}
							coord.add(new Coordinate(shape.getCoordinates().get(shape.getCoordinates().size() - 1).x,
									frame.getMaxY()));
						} else {
							coord.add(new Coordinate(shape.getCoordinates().get(shape.getCoordinates().size() - 1).x,
									frame.getMaxY()));
							for (Coordinate c : shape.getCoordinates()) {
								coord.add(new Coordinate(c.x, c.y));
							}
							coord.add(new Coordinate(frame.getMinX(), shape.getCoordinates().get(0).y));
						}
						coord.add(new Coordinate(frame.getMinX(), frame.getMaxY()));
					}
				}
				if (shape.getGeometry().getDirection().equals("NE")) {
					if (shape.getCoordinates().size() == 2) {
						Coordinate p1 = createLine(shape.getCoordinates().get(0),
								shape.getCoordinates().get(shape.getCoordinates().size() - 1), frame).get(0);
						Coordinate p2 = createLine(shape.getCoordinates().get(0),
								shape.getCoordinates().get(shape.getCoordinates().size() - 1), frame).get(1);
						coord.add(new Coordinate(p1.x, p1.y));
						coord.add(new Coordinate(p2.x, p2.y));
						if (p1.y > p2.y) {
							coord.add(new Coordinate(p2.x, Math.max(p1.y, frame.getMaxY())));
							coord.add(new Coordinate(p1.x, Math.max(p1.y, frame.getMaxY())));
						} else {
							coord.add(new Coordinate(p2.x, Math.max(p2.y, frame.getMaxY())));
							coord.add(new Coordinate(p1.x, Math.max(p2.y, frame.getMaxY())));
						}
						coord.add(new Coordinate(p1.x, p1.y));
					} else {
						coord.add(new Coordinate(frame.getMaxX(), frame.getMaxY()));
						if (shape.getCoordinates().get(0).y > shape.getCoordinates()
								.get(shape.getCoordinates().size() - 1).y) {
							coord.add(new Coordinate(shape.getCoordinates().get(0).x, frame.getMaxY()));
							for (Coordinate c : shape.getCoordinates()) {
								coord.add(new Coordinate(c.x, c.y));
							}
							coord.add(new Coordinate(frame.getMaxX(),
									shape.getCoordinates().get(shape.getCoordinates().size() - 1).y));
						} else {
							coord.add(new Coordinate(frame.getMaxX(),
									shape.getCoordinates().get(shape.getCoordinates().size() - 1).y));
							for (Coordinate c : shape.getCoordinates()) {
								coord.add(new Coordinate(c.x, c.y));
							}
							coord.add(new Coordinate(shape.getCoordinates().get(0).x, frame.getMaxY()));
						}
						coord.add(new Coordinate(frame.getMaxX(), frame.getMaxY()));
					}
				}
				if (shape.getGeometry().getDirection().equals("SW")) {
					if (shape.getCoordinates().size() == 2) {
						Coordinate p1 = createLine(shape.getCoordinates().get(0),
								shape.getCoordinates().get(shape.getCoordinates().size() - 1), frame).get(0);
						Coordinate p2 = createLine(shape.getCoordinates().get(0),
								shape.getCoordinates().get(shape.getCoordinates().size() - 1), frame).get(1);
						coord.add(new Coordinate(p1.x, frame.getMinY()));
						coord.add(new Coordinate(p1.x, p1.y));
						coord.add(new Coordinate(p2.x, p2.y));
						coord.add(new Coordinate(p2.x, frame.getMinY()));
						coord.add(new Coordinate(p1.x, frame.getMinY()));
					} else {
						coord.add(new Coordinate(frame.getMinX(), frame.getMinY()));
						if (shape.getCoordinates().get(0).y > shape.getCoordinates()
								.get(shape.getCoordinates().size() - 1).y) {
							coord.add(new Coordinate(frame.getMinX(), shape.getCoordinates().get(0).y));
							for (Coordinate c : shape.getCoordinates()) {
								coord.add(new Coordinate(c.x, c.y));
							}
							coord.add(new Coordinate(shape.getCoordinates().get(shape.getCoordinates().size() - 1).x,
									frame.getMinY()));
						} else {
							coord.add(new Coordinate(shape.getCoordinates().get(shape.getCoordinates().size() - 1).x,
									frame.getMinY()));
							for (Coordinate c : shape.getCoordinates()) {
								coord.add(new Coordinate(c.x, c.y));
							}
							coord.add(new Coordinate(frame.getMinX(), shape.getCoordinates().get(0).y));
						}
						coord.add(new Coordinate(frame.getMinX(), frame.getMinY()));
					}
				}
				if (shape.getGeometry().getDirection().equals("SE")) {
					if (shape.getCoordinates().size() == 2) {
						Coordinate p1 = createLine(shape.getCoordinates().get(0),
								shape.getCoordinates().get(shape.getCoordinates().size() - 1), frame).get(0);
						Coordinate p2 = createLine(shape.getCoordinates().get(0),
								shape.getCoordinates().get(shape.getCoordinates().size() - 1), frame).get(1);
						coord.add(new Coordinate(p1.x, frame.getMinY()));
						coord.add(new Coordinate(p1.x, p1.y));
						coord.add(new Coordinate(p2.x, p2.y));
						coord.add(new Coordinate(p2.x, frame.getMinY()));
						coord.add(new Coordinate(p1.x, frame.getMinY()));
					} else {
						if (frame.getMaxX() > 0) {
							for (Coordinate c : shape.getCoordinates()) {
								if (c.x < 0) {
									c.x = frame.getMaxX();
								}
							}
						}
						coord.add(new Coordinate(frame.getMaxX(), frame.getMinY()));
						if (shape.getCoordinates().get(0).y < shape.getCoordinates()
								.get(shape.getCoordinates().size() - 1).y) {
							coord.add(new Coordinate(shape.getCoordinates().get(0).x, frame.getMinY()));
							for (Coordinate c : shape.getCoordinates()) {
								coord.add(new Coordinate(c.x, c.y));
							}
							coord.add(new Coordinate(frame.getMaxX(),
									shape.getCoordinates().get(shape.getCoordinates().size() - 1).y));
						} else {
							coord.add(new Coordinate(frame.getMaxX(), shape.getCoordinates().get(0).y));
							for (Coordinate c : shape.getCoordinates()) {
								coord.add(new Coordinate(c.x, c.y));
							}
							coord.add(new Coordinate(shape.getCoordinates().get(shape.getCoordinates().size() - 1).x,
									frame.getMinY()));
						}
						coord.add(new Coordinate(frame.getMaxX(), frame.getMinY()));
					}
				}
				if (shape.getGeometry().getDirection().equals("W")) {
					if (shape.getCoordinates().size() == 2) {
						Coordinate p1 = createLine(shape.getCoordinates().get(0),
								shape.getCoordinates().get(shape.getCoordinates().size() - 1), frame).get(0);
						Coordinate p2 = createLine(shape.getCoordinates().get(0),
								shape.getCoordinates().get(shape.getCoordinates().size() - 1), frame).get(1);
						coord.add(new Coordinate(frame.getMinX(), p1.y));
						coord.add(new Coordinate(p1.x, p1.y));
						coord.add(new Coordinate(p2.x, p2.y));
						coord.add(new Coordinate(frame.getMinX(), p2.y));
						coord.add(new Coordinate(frame.getMinX(), p1.y));

					} else {
						coord.add(new Coordinate(frame.getMinX(), shape.getCoordinates().get(0).y));
						for (Coordinate c : shape.getCoordinates()) {
							coord.add(new Coordinate(c.x, c.y));
						}
						coord.add(new Coordinate(frame.getMinX(),
								shape.getCoordinates().get(shape.getCoordinates().size() - 1).y));
						coord.add(new Coordinate(frame.getMinX(), shape.getCoordinates().get(0).y));
					}
				}
				if (shape.getGeometry().getDirection().equals("E")) {
					if (shape.getCoordinates().size() == 2) {
						Coordinate p1 = createLine(shape.getCoordinates().get(0),
								shape.getCoordinates().get(shape.getCoordinates().size() - 1), frame).get(0);
						Coordinate p2 = createLine(shape.getCoordinates().get(0),
								shape.getCoordinates().get(shape.getCoordinates().size() - 1), frame).get(1);
						coord.add(new Coordinate(frame.getMaxX(), p1.y));
						coord.add(new Coordinate(p1.x, p1.y));
						coord.add(new Coordinate(p2.x, p2.y));
						coord.add(new Coordinate(frame.getMaxX(), p2.y));
						coord.add(new Coordinate(frame.getMaxX(), p1.y));
					} else {
						coord.add(new Coordinate(frame.getMaxX(), shape.getCoordinates().get(0).y));
						for (Coordinate c : shape.getCoordinates()) {
							coord.add(new Coordinate(c.x, c.y));
						}
						coord.add(new Coordinate(frame.getMaxX(),
								shape.getCoordinates().get(shape.getCoordinates().size() - 1).y));
						coord.add(new Coordinate(frame.getMaxX(), shape.getCoordinates().get(0).y));
					}
				}
			}
		}
		return coord.toArray(new Coordinate[coord.size()]);
	}

	private ArrayList<Coordinate> createLine(Coordinate p1, Coordinate p2, Envelope frame) {
		ArrayList<Coordinate> coord = new ArrayList<>();
		double a = p1.y - p2.y;
		double b = p2.x - p1.x;
		double c = p1.x * p2.y - p2.x * p1.y;
		p1.x = frame.getMinX();
		p1.y = (-c - a * p1.x) / b;
		p2.x = frame.getMaxX();
		p2.y = (-c - a * p2.x) / b;
		coord.add(p1);
		coord.add(p2);
		return coord;
	}

	private List<GTCalculatedRegion> postProcessFir(FeatureCollection resultFeatures) {
		ArrayList<GTCalculatedRegion> resultRegion = new ArrayList<>();
		List<Feature> lf = resultFeatures.getFeatures();
		for (Feature f : lf) {
			if (f.getGeometry() instanceof Polygon) {
				Polygon pl = (Polygon) f.getGeometry();
				boolean nan = false;
				GTCalculatedRegion r = new GTCalculatedRegion();
				for (List<LngLatAlt> plst : pl.getCoordinates()) {
					for (LngLatAlt p : plst) {
						if (Double.isNaN(p.getLatitude()) || Double.isNaN(p.getLongitude())) {
							nan = true;
							break;
						} else {
							Coordinate c = transformCoordinateBack(new Coordinate(p.getLongitude(), p.getLatitude()));
							if (latFirst) {
								r.getCoordinates().add(BigDecimal.valueOf(c.y)
										.setScale(scaleCoord, RoundingMode.HALF_UP).doubleValue());
								r.getCoordinates().add(BigDecimal.valueOf(c.x)
										.setScale(scaleCoord, RoundingMode.HALF_UP).doubleValue());
							} else {
								r.getCoordinates().add(BigDecimal.valueOf(c.x)
										.setScale(scaleCoord, RoundingMode.HALF_UP).doubleValue());
								r.getCoordinates().add(BigDecimal.valueOf(c.y)
										.setScale(scaleCoord, RoundingMode.HALF_UP).doubleValue());
							}
						}
					}
				}
				if (nan) {
					continue;
				}
				resultRegion.add(r);
			}
		}
		return resultRegion;
	}

	private String postProcessFirJson(FeatureCollection resultFeatures) {
		List<Feature> lf = resultFeatures.getFeatures();
		FeatureCollection featureFinal = new FeatureCollection();
		for (Feature f : lf) {
			if (f.getGeometry() instanceof Polygon) {
				Polygon pl = (Polygon) f.getGeometry();
				boolean nan = false;
				for (List<LngLatAlt> plst : pl.getCoordinates()) {
					for (LngLatAlt p : plst) {
						if (Double.isNaN(p.getLatitude()) || Double.isNaN(p.getLongitude())) {
							nan = true;
							break;
						}
					}
				}
				if (nan) {
					continue;
				}
				featureFinal.add(f);
			}
		}
		try {
			for (Feature f : featureFinal) {
				Polygon pl = (Polygon) f.getGeometry();
				for (List<LngLatAlt> plst : pl.getCoordinates()) {
					for (LngLatAlt p : plst) {
						Coordinate c = transformCoordinateBack(new Coordinate(p.getLongitude(), p.getLatitude()));
						p.setLongitude(
								BigDecimal.valueOf(c.x).setScale(scaleCoord, RoundingMode.HALF_UP).doubleValue());
						p.setLatitude(BigDecimal.valueOf(c.y).setScale(scaleCoord, RoundingMode.HALF_UP).doubleValue());
					}
				}
			}
			return new ObjectMapper().writeValueAsString(featureFinal);
		} catch (Exception e) {
			logger.error("postProcessFirJson(): ", e);
		}
		return "";
	}

	/**
	 * Creates a list of polygons which consist of FIR boundaries and intersection
	 * with given polygon (for example, from sigmet/airmet)*
	 * 
	 * @return linked list of regions with their points in EPSG:4326 format inside
	 * @throws GeoServiceException
	 */
	public List<GTCalculatedRegion> recalcFromPolygon(String firIcao, LinkedList<GTCoordPoint> points)
			throws GeoServiceException {
		// search for fir in data store
		SimpleFeatureCollection features = this.getFirFeatures(firIcao);
		if (features == null || features.size() == 0) {
			logger.error("fir shape not found: " + firIcao);
			throw new GeoServiceException("fir shape not found: " + firIcao);
		}

		// repack sigmet model
		ArrayList<Shape> shapes = this.convertModelPoints(points);
		if (shapes.size() == 0) {
			logger.error("error in source model (points): " + points + " " + firIcao);
			throw new GeoServiceException("error in source model (points): " + points + " " + firIcao);
		}

		// render fir intersection
		FeatureCollection resultFeatures = this.renderFir(features, shapes);
		if (resultFeatures == null || resultFeatures.getFeatures().size() == 0) {
			logger.error("no result feature created: " + points + " " + firIcao);
			throw new GeoServiceException("no result feature created: " + points + " " + firIcao);
		}

		// post process result data
		List<GTCalculatedRegion> resultData = this.postProcessFir(resultFeatures);
		if (resultData == null || resultData.size() == 0) {
			logger.error("post process error in data: " + points + " " + firIcao);
			throw new GeoServiceException("post process error in data: " + points + " " + firIcao);
		}

		// logger.info("result data created: " + points + " " + firIcao + " " +
		// resultData);

		return resultData;
	}

	/**
	 * @return calculated area of entire fir
	 * @throws GeoServiceException
	 * 
	 **/
	public List<GTCalculatedRegion> recalcEntireFir(String firIcao) throws GeoServiceException {

		// search for fir in data store
		SimpleFeatureCollection features = this.getFirFeatures(firIcao);
		if (features == null || features.size() == 0) {
			logger.error("fir shape not found: " + firIcao);
			throw new GeoServiceException("fir shape not found: " + firIcao);
		}

		// render entire fir
		FeatureCollection resultFeatures = new FeatureCollection();
		this.addFir(features, resultFeatures);
		if (resultFeatures == null || resultFeatures.getFeatures().size() == 0) {
			logger.error("no result feature created: " + firIcao);
			throw new GeoServiceException("no result feature created: " + firIcao);
		}

		// post process result data
		List<GTCalculatedRegion> resultData = this.postProcessFir(resultFeatures);
		if (resultData == null || resultData.size() == 0) {
			logger.error("post process error in data: " + firIcao);
			throw new GeoServiceException("post process error in data: " + firIcao);
		}

		// logger.info("result data created: " + firIcao + " " + resultData);

		return resultData;
	}

	/**
	 * @return GeoJSON representation of regions with their points in EPSG:3857
	 *         format inside
	 * @throws GeoServiceException
	 **/
	public String jsonFromPolygon(String firIcao, LinkedList<GTCoordPoint> points) throws GeoServiceException {

		// search for fir in data store
		SimpleFeatureCollection features = this.getFirFeatures(firIcao);
		if (features == null || features.size() == 0) {
			logger.error("fir shape not found: " + firIcao);
			throw new GeoServiceException("fir shape not found: " + firIcao);
		}

		// repack sigmet model
		ArrayList<Shape> shapes = this.convertModelPoints(points);
		if (shapes.size() == 0) {
			logger.error("error in source model (points): " + points + " " + firIcao);
			throw new GeoServiceException("error in source model (points): " + points + " " + firIcao);
		}

		// render fir intersection
		FeatureCollection resultFeatures = this.renderFir(features, shapes);
		if (resultFeatures == null || resultFeatures.getFeatures().size() == 0) {
			logger.error("no result feature created: " + points + " " + firIcao);
			throw new GeoServiceException("no result feature created: " + points + " " + firIcao);
		}

		// post process result data
		String resultData = this.postProcessFirJson(resultFeatures);
		if (resultData.isEmpty()) {
			logger.error("post process error in data: " + points + " " + firIcao);
			throw new GeoServiceException("post process error in data: " + points + " " + firIcao);
		}

		// logger.info("result data created: " + points + " " + firIcao + " " +
		// resultData);

		return resultData;

	}

	public String jsonFromPoint(GTCoordPoint point, double diameterInMeters) throws GeoServiceException {
		String resultData = "";
		Coordinate cr = new Coordinate(
				(point.getLongitude().getAzimuth().equals("E") ? 1 : -1)
						* (point.getLongitude().getDeg() + point.getLongitude().getMin() / 60.0),
				(point.getLatitude().getAzimuth().equals("N") ? 1 : -1)
						* (point.getLatitude().getDeg() + point.getLatitude().getMin() / 60.0));

		try {
			Feature c = createCircle(cr.y, cr.x, diameterInMeters);
			FeatureCollection resultFeatures = new FeatureCollection();
			resultFeatures.add(c);
			resultData = this.postProcessFirJson(resultFeatures);
			if (resultData.isEmpty()) {
				logger.error("post process error in jsonFromPoint: " + point + " " + diameterInMeters);
				throw new GeoServiceException("post process error in jsonFromPoint: " + point + " " + diameterInMeters);
			}
		} catch (Exception e) {
			logger.error("jsonFromPoint(): ", e);
			throw new GeoServiceException("error in creating circle: " + e + point + " " + diameterInMeters);
		}
		return resultData;
	}

	/**
	 * @return GeoJSON representation of entire fir
	 * @throws GeoServiceException
	 * 
	 **/
	public String jsonEntireFir(String firIcao) throws GeoServiceException {
		// search for fir in data store
		SimpleFeatureCollection features = this.getFirFeatures(firIcao);
		if (features == null || features.size() == 0) {
			logger.error("fir shape not found: " + firIcao);
			throw new GeoServiceException("fir shape not found: " + firIcao);
		}

		// render fir intersection
		FeatureCollection resultFeatures = new FeatureCollection();
		this.addFir(features, resultFeatures);
		if (resultFeatures == null || resultFeatures.getFeatures().size() == 0) {
			logger.error("no result feature created: " + firIcao);
			throw new GeoServiceException("no result feature created: " + firIcao);
		}

		// post process result data
		String resultData = this.postProcessFirJson(resultFeatures);
		if (resultData.isEmpty()) {
			logger.error("post process error in data: " + firIcao);
			throw new GeoServiceException("post process error in data: " + firIcao);
		}

		// logger.info("result data created: " + firIcao + " " + resultData);

		return resultData;

	}

	/**
	 * Creates a list of polygons which consist of FIR boundaries and intersection
	 * with given lines (for example, from sigmet/airmet)
	 * 
	 * @return linked list of regions with their points in EPSG:4326 format inside
	 * @throws GeoServiceException
	 **/
	public List<GTCalculatedRegion> recalcFromLines(String firIcao, LinkedList<GTDirectionFromLine> lines)
			throws GeoServiceException {

		// search for fir in data store
		SimpleFeatureCollection features = this.getFirFeatures(firIcao);
		if (features == null || features.size() == 0) {
			logger.error("fir shape not found: " + firIcao);
			throw new GeoServiceException("fir shape not found: " + firIcao);
		}

		// repack sigmet model
		ArrayList<Shape> shapes = this.convertModelLines(lines);
		if (shapes.size() == 0) {
			logger.error("error in source model (lines): " + lines + " " + firIcao);
			throw new GeoServiceException("error in source model (lines): " + lines + " " + firIcao);
		}

		// render fir intersection
		FeatureCollection resultFeatures = this.renderFir(features, shapes);
		if (resultFeatures == null || resultFeatures.getFeatures().size() == 0) {
			logger.error("no result feature created: " + lines + " " + firIcao);
			throw new GeoServiceException("no result feature created: " + lines + " " + firIcao);
		}

		// post process result data
		List<GTCalculatedRegion> resultData = this.postProcessFir(resultFeatures);
		if (resultData == null || resultData.size() == 0) {
			logger.error("post process error in data: " + lines + " " + firIcao);
			throw new GeoServiceException("post process error in data: " + lines + " " + firIcao);
		}

		// logger.info("result data created: " + lines + " " + firIcao + " " +
		// resultData);

		return resultData;

	}

	/**
	 * Creates a list of polygons which consist of FIR boundaries and intersection
	 * with given lines (for example, from sigmet/airmet)
	 * 
	 * @return GeoJSON representation of regions with their points in EPSG:4326
	 *         format
	 * @throws GeoServiceException
	 */
	public String jsonFromLines(String firIcao, LinkedList<GTDirectionFromLine> lines) throws GeoServiceException {

		// search for fir in data store
		SimpleFeatureCollection features = this.getFirFeatures(firIcao);
		if (features == null || features.size() == 0) {
			logger.error("fir shape not found: " + firIcao);
			throw new GeoServiceException("fir shape not found: " + firIcao);
		}

		// repack sigmet model
		ArrayList<Shape> shapes = this.convertModelLines(lines);
		if (shapes.size() == 0) {
			logger.error("error in source model (lines): " + lines + " " + firIcao);
			throw new GeoServiceException("error in source model (lines): " + lines + " " + firIcao);

		}

		// render fir intersection
		FeatureCollection resultFeatures = this.renderFir(features, shapes);
		if (resultFeatures == null || resultFeatures.getFeatures().size() == 0) {
			logger.error("no result feature created: " + lines + " " + firIcao);
			throw new GeoServiceException("no result feature created: " + lines + " " + firIcao);

		}

		// post process result data
		String resultData = this.postProcessFirJson(resultFeatures);
		if (resultData.isEmpty()) {
			logger.error("post process error in data: " + lines + " " + firIcao);
			throw new GeoServiceException("post process error in data: " + lines + " " + firIcao);

		}

		// logger.info("result data created: " + lines + " " + firIcao + " " +
		// resultData);

		return resultData;
	}

	/** Converts lat long point to decimal coordinates */
	public GTCalculatedRegion recalcFromSinglePoint(GTCoordPoint coordPoint) {
		GTCalculatedRegion r = new GTCalculatedRegion();
		r.getCoordinates()
				.add(BigDecimal.valueOf(coordPoint.getLatitude().getDeg() + coordPoint.getLatitude().getMin() / 60.0)
						.setScale(scaleCoord, RoundingMode.HALF_UP).doubleValue());
		r.getCoordinates()
				.add(BigDecimal.valueOf(coordPoint.getLongitude().getDeg() + coordPoint.getLongitude().getMin() / 60.0)
						.setScale(scaleCoord, RoundingMode.HALF_UP).doubleValue());
		return r;
	}

	private Feature createCircle(double latitude, double longitude, double diameterInMeters)
			throws MismatchedDimensionException, TransformException {
		GeometricShapeFactory shapeFactory = new GeometricShapeFactory();
		shapeFactory.setNumPoints(64);
		shapeFactory.setCentre(new Coordinate(latitude, longitude));
		shapeFactory.setWidth(diameterInMeters / 111320d);
		shapeFactory.setHeight(diameterInMeters / (40075000 * Math.cos(Math.toRadians(latitude)) / 360));

		Feature f = new Feature();
		 org.locationtech.jts.geom.Polygon circle = shapeFactory.createEllipse();
		ArrayList<LngLatAlt> l = new ArrayList<>();
		for (int i = 0; i < circle.getCoordinates().length; i++) {
			 org.locationtech.jts.geom.Point pt = ( org.locationtech.jts.geom.Point) JTS.transform(
					(Geometry) geometryFactory
							.createPoint(new Coordinate(circle.getCoordinates()[i].y,
									circle.getCoordinates()[i].x)),
					coordTransform);
			l.add(new LngLatAlt(pt.getX(), pt.getY()));
		}
		Polygon p = new Polygon(l);
		f.setGeometry(p);
		return f;
	}

}
